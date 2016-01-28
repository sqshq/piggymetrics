var global = {
    mobileClient: false,
    savePermit: true,
    usd: 0,
    eur: 0
}

$(window).load(function(){

	if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		FastClick.attach(document.body);
        global.mobileClient = true;
	}

    $.getJSON("http://api.fixer.io/latest?base=RUB", function( data ) {
        global.eur = 1 / data.rates.EUR;
        global.usd = 1 / data.rates.USD;
    });

	var userIsLoggedIn = true;

	if (userIsLoggedIn) {
		showGreetingPage();
	} else {
		showLoginForm();
	}
});

function showGreetingPage() {
	testFillObjects();
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