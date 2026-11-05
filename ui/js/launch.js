var global = {
    mobileClient: false,
    savePermit: true,
    usd: 0,
    eur: 0
};

/**
 * Amazon Cognito user pool authentication (replaces the auth-service password grant).
 * window.PIGGY_CONFIG is written to js/config.js at deploy time.
 */

var cognitoConfig = window.PIGGY_CONFIG || {};

function cognitoRequest(action, body, onSuccess, onError) {
	$.ajax({
		url: 'https://cognito-idp.' + cognitoConfig.region + '.amazonaws.com/',
		type: 'post',
		dataType: 'json',
		async: false,
		contentType: 'application/x-amz-json-1.1',
		headers: {'X-Amz-Target': 'AWSCognitoIdentityProviderService.' + action},
		data: JSON.stringify(body),
		success: onSuccess,
		error: onError
	});
}

function signUp(username, password, onSuccess, onError) {
	cognitoRequest('SignUp', {
		ClientId: cognitoConfig.userPoolClientId,
		Username: username,
		Password: password
	}, onSuccess, onError);
}

// Idempotent: creates the PiggyMetrics account for the signed-in Cognito user if missing.
function initializeAccount() {
	var success = false;
	$.ajax({
		url: 'accounts/current',
		type: 'post',
		async: false,
		headers: {'Authorization': 'Bearer ' + getOauthTokenFromStorage()},
		success: function () {
			success = true;
		}
	});
	return success;
}

function requestOauthToken(username, password) {

	var success = false;

	cognitoRequest('InitiateAuth', {
		AuthFlow: 'USER_PASSWORD_AUTH',
		ClientId: cognitoConfig.userPoolClientId,
		AuthParameters: {USERNAME: username, PASSWORD: password}
	}, function (data) {
		if (data.AuthenticationResult) {
			localStorage.setItem('token', data.AuthenticationResult.AccessToken);
			success = initializeAccount();
		}
		if (!success) {
			removeOauthTokenFromStorage();
		}
	}, function () {
		removeOauthTokenFromStorage();
	});

	return success;
}

function getOauthTokenFromStorage() {
	return localStorage.getItem('token');
}

function removeOauthTokenFromStorage() {
    return localStorage.removeItem('token');
}

/**
 * Current account
 */

function getCurrentAccount() {

	var token = getOauthTokenFromStorage();
	var account = null;

	if (token) {
		$.ajax({
			url: 'accounts/current',
			datatype: 'json',
			type: 'get',
			headers: {'Authorization': 'Bearer ' + token},
			async: false,
			success: function (data) {
				account = data;
			},
			error: function () {
				removeOauthTokenFromStorage();
			}
		});
	}

	return account;
}

$(window).load(function(){

	if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		FastClick.attach(document.body);
        global.mobileClient = true;
	}

    // Rates are units per USD; the dashboard expects rubles per unit.
    $.getJSON("statistics/rates", function( rates ) {
        global.eur = rates.RUB / rates.EUR;
        global.usd = rates.RUB;
    });

	var account = getCurrentAccount();

	if (account) {
		showGreetingPage(account);
	} else {
		showLoginForm();
	}
});

function showGreetingPage(account) {
    initAccount(account);
	var userAvatar = $("<img />").attr("src","images/userpic.jpg");
	$(userAvatar).load(function() {
		setTimeout(initGreetingPage, 500);
	});
}

function showLoginForm() {
	$("#loginpage").show();
	$("#frontloginform").focus();
	setTimeout(initialShaking, 700);
}