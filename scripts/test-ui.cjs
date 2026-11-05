// Test the browser's Cognito request contract without contacting AWS.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');

function browser() {
    const calls = [];
    const storage = new Map();
    let failAccount = false;
    const jquery = () => ({load() {}});
    jquery.ajax = request => {
        calls.push(request);
        if (request.headers['X-Amz-Target']?.endsWith('.SignUp')) {
            request.success({});
        } else if (request.headers['X-Amz-Target']?.endsWith('.InitiateAuth')) {
            request.success({AuthenticationResult: {AccessToken: 'test-access-token'}});
        } else if (request.url === 'accounts/current' && !failAccount) {
            request.success({});
        }
    };
    const context = vm.createContext({
        window: {PIGGY_CONFIG: {region: 'us-east-1', userPoolClientId: 'browser'}},
        $: jquery,
        localStorage: {
            setItem: (key, value) => storage.set(key, value),
            getItem: key => storage.get(key),
            removeItem: key => storage.delete(key)
        }
    });
    vm.runInContext(fs.readFileSync(path.join(__dirname, '../ui/js/launch.js'), 'utf8'), context);
    return {context, calls, storage, failAccount: () => {failAccount = true;}};
}

const signup = browser();
let signedUp = false;
signup.context.signUp('alice', 'example-password', () => {signedUp = true;}, () => {});
assert.ok(signedUp);
assert.equal(signup.calls[0].url, 'https://cognito-idp.us-east-1.amazonaws.com/');
assert.equal(signup.calls[0].headers['X-Amz-Target'], 'AWSCognitoIdentityProviderService.SignUp');
assert.deepEqual(JSON.parse(signup.calls[0].data),
    {ClientId: 'browser', Username: 'alice', Password: 'example-password'});

const login = browser();
assert.equal(login.context.requestOauthToken('alice', 'example-password'), true);
assert.equal(login.storage.get('token'), 'test-access-token');
assert.equal(JSON.parse(login.calls[0].data).AuthFlow, 'USER_PASSWORD_AUTH');
assert.equal(login.calls[1].url, 'accounts/current');
assert.equal(login.calls[1].type, 'post');
assert.equal(login.calls[1].headers.Authorization, 'Bearer test-access-token');

const failed = browser();
failed.failAccount();
assert.equal(failed.context.requestOauthToken('alice', 'example-password'), false);
assert.equal(failed.storage.has('token'), false);
login.context.removeOauthTokenFromStorage();
assert.equal(login.storage.has('token'), false);
console.log('PASS browser Cognito requests, account initialization and token cleanup');
