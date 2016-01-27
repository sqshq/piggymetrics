// Launch after authorization
$(window).load(function() {
	$("#preloader, #lastlogo").show();
	var userAvatar = $("<img />").attr("src","images/userpic.jpg");
	$(userAvatar).load(function() {
		setTimeout(initGreetingPage, 500);
	});
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		is_mobile = true;
		FastClick.attach(document.body);
	}
	else {
		is_mobile = false;
	}

	testFillObjects();
}); 