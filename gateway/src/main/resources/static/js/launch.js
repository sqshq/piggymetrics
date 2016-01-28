var is_mobile = false;
var savePermit=true;

$(window).load(function(){

	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		FastClick.attach(document.body);
		is_mobile = true;
	}

	var user_logged_in = false;

	if (user_logged_in) {
		launchApplication();
	} else {
		showLoginForm();
	}
});

function launchApplication() {
	testFillObjects();
	$("#preloader, #lastlogo").show();
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
