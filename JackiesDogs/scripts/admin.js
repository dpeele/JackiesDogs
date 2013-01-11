var admin = {};

admin.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("#admin").height($(window).height() - ($("#admin").offset().top + 75));
        $("#leftAdmin").height($(window).height() - ($("#admin").offset().top + 65));
        $("#rightAdmin").height($(window).height() - ($("#admin").offset().top + 65));
    });
    $(window).resize();	
	
    //dialogs
	$("div.dialog").hide(); //set all dialog divs to not be visible
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $(":input").addClass("ui-widget").attr("name",$(this).attr("id"));
    
    //buttons
    $("#uploadPricelistButton").button().click(function () {admin.executeAdminTask("pricelist");});
    $("#uploadInvoiceButton").button().click(function () {admin.executeAdminTask("invoice");});
    $("#scrapeSiteButton").button().click(function () {admin.executeAdminTask("url");});
};

admin.executeAdminTask = function (input) {
	var parameter;
	var div;
	switch (input) {
		case ("pricelist"):
			parameter = "&command=uploadProducts";
			div = "missingFilename";;
			break;
		case ("invoice"):
			parameter = "&command=uploadInvoice";
			div = "missingFilename";
			break;			
		case ("url"):
			parameter = "&command=scrape";
			div = "missingWebSite";
			break;
	}
	if ($("#"+input).val().length > 0) {
		$.ajax({
			url: "admin",
			dataType: "html",
			cache: false,
			type: "post",                
			data: $("#adminForm").serialize(),
			success: function( data ) {
				$("#adminTableDiv").html(data);	
			}
		});
	} else {
		$("#"+div).dialog();
	}
};
