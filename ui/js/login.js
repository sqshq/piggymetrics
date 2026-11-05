/**
 * Registration form
 */

$('#signup').submit(function(e) {

    e.preventDefault();

    var username = $("input[id='backloginform']").val();
    var password = $("input[id='backpasswordform']").val();

    if (username.length < 3 || username.length > 20 || password.length < 8) {
        alert("Username must be 3 to 20 characters and password - at least 8. Be tricky!");
        return;
    }

	if (username && password) {
        signUp(username, password, function () {

                if (!requestOauthToken(username, password)) {
                    alert("An error during account creation. Please, try again.");
                    return;
                }
                initAccount(getCurrentAccount());

                $('#registrationforms, .fliptext, #createaccount').fadeOut(300);
                $('#mailform').fadeIn(500);
                setTimeout(function(){ $("#backmailform").focus() }, 10);
            }, function (xhr) {
                var type = xhr.responseJSON && xhr.responseJSON.__type;
                if (type == 'UsernameExistsException') {
                    alert("Sorry, account with the same name already exists.");
                } else if (type == 'InvalidPasswordException' || type == 'InvalidParameterException') {
                    alert("Sorry, this username or password is not allowed.");
                } else {
                    alert("An error during account creation. Please, try again.");
                }
        });

	} else {
        alert("Please, fill all the fields.");
    }
});

/**
 * E-mail form
 */

$('#mail').submit(function(e) {

	e.preventDefault();

    var email = $("input[name='usermail']").val();

	if (email) {
        $.ajax({
            url: 'notifications/recipients/current',
            datatype: 'json',
            type: 'put',
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                scheduledNotifications: {
                    "REMIND": {
                        "active": true,
                        "frequency": "MONTHLY"
                    }
                }
            }),
            headers: {'Authorization': 'Bearer ' + getOauthTokenFromStorage()},
            async: true,
            success: function () {
                setTimeout(initGreetingPage, 200);
                setTimeout(function(){ $('#backmailform').val(''); $("#lastlogo").show(); }, 300);
            },
            error: function (xhr, ajaxOptions, thrownError) {
                if (xhr.status == 400) {
                    alert("Sorry, it seems your email address is invalid.");
                } else {
                    alert("An error during saving notifications options");
                }
            }
        });
	}
});

/**
 * Login
 */

function login() {

	$("#piggy").toggleClass("loadingspin");
	$("#secondenter").hide();
	$("#preloader, #lastlogo").show();

    var username = $("input[id='frontloginform']").val();
    var password = $("input[id='frontpasswordform']").val();

    if (requestOauthToken(username, password)) {

        initAccount(getCurrentAccount());

        var userAvatar = $("<img />").attr("src","images/userpic.jpg");
        $(userAvatar).load(function() {
            setTimeout(initGreetingPage, 500);
        });
    } else {
        $("#preloader, #enter, #secondenter").hide();
        flipForm();
        $('.frontforms').val('');
        $("#frontloginform").focus();
        alert("Something went wrong. Please, check your credentials");
    }
}

/**
 * Logout
 */

function logout() {
    removeOauthTokenFromStorage();
    location.reload();
}

/**
 * Demo
 */

$(".demobutton").bind("click", function(){
    $.ajax({
        url: 'accounts/demo',
        datatype: 'json',
        type: 'get',
        async: false,
        success: function (data) {

            global.savePermit = false;
            initAccount(data);

            var userAvatar = $("<img />").attr("src","images/userpic.jpg");
            $(userAvatar).load(function() {
                setTimeout(initGreetingPage, 500);
            });
        },
        error: function () {
            alert("Something went wrong. Please, try again");
        }
    });
});

$("#skipmail").bind("click", function(){
    $("#lastlogo").show();
    setTimeout(initGreetingPage, 300);
});

/**
 * Login form effects
 */

function initialShaking(){
	autoShake();
	setTimeout(autoShake, 1900);
}

function autoShake() {
	$("#piggy").toggleClass("auto-shake");
}

function OnHoverShaking() {
	hoverShake();
	setTimeout(hoverShake, 1700);
}

function hoverShake() {
	$("#piggy").toggleClass("hover-shake");
}

function toggleInfo() {
	$("#infopage").toggle();
}

function flipForm() {
	$("#cube").toggleClass("flippedform");
	$("#frontpasswordform").focus();
}

$("#piggy").on("click mouseover", function(){
	if ($(this).hasClass("skakelogo") === false && $(this).hasClass("hover-shake") === false) {
		OnHoverShaking();
	}
});

$(".fliptext").bind("click", function(){
	setTimeout( function() { $("#plusavatar").addClass("avataranimation"); } , 1000);
	$("#flipper").toggleClass("flippedcard");
});

$(".flipinfo").on("click", function() {
	$("#flipper").toggleClass("flippedcardinfo");
	toggleInfo();
});

$(".frominfo, #infotitle, #infosubtitle").on("click", function() {
	$("#flipper").toggleClass("flippedcardinfo");
	setTimeout(toggleInfo, 400);
});

$("#enter").on("click", function() {flipForm()});
$("#secondenter").on("click", function() {login()});

$("#frontloginform").keyup(function (e) {
	if( $(this).val().length >= 3 ) {
		$("#enter").show();
		if (e.which == 13) {
			flipForm();
			$("#enter").hide();
		}
		return;
	} else {
		$("#enter").hide();
	}
});

$("#frontpasswordform").keyup(function(e) {
	if ( $(this).val().length >= 6) {
		$("#secondenter").show();
		if(e.which == 13) {
			$(this).blur();
            login();
		}
		return;
	} else {
		$("#secondenter").hide();
	}
});