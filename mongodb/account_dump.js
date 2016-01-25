/**
 * Creates demo user and account collection index
 */

print('dump start');

db.accounts.drop(); // dev mode

db.accounts.createIndex({
   "user.username":1
},
{ 
   unique:true
});

db.accounts.insert({
   user:{
      username:"demo",
      password:"$2a$04$Fp4saQmhvUMsWoG3r3pkM.7/RTWfmqgDWmkWydPfrT0su15U4tvkq"
   }
});

print('dump complete');