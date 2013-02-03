var admin = {};

admin.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("div#adminPanel #admin").height($(window).height() - ($("div#adminPanel #admin").offset().top + 75));
        $("div#adminPanel #leftAdmin").height($(window).height() - ($("div#adminPanel #admin").offset().top + 65));
        $("div#adminPanel #rightAdmin").height($(window).height() - ($("div#adminPanel #admin").offset().top + 65));
    });
    $(window).resize();	
    
    //set clicked attr of button that submits form
    $("form input[type=submit]").click(function() {
        $("input[type=submit]", $(this).parents("form")).removeAttr("clicked");
        $(this).attr("clicked", "true");
    });    
	
    //dialogs
	$("div#adminPanel div.dialog").hide(); //set all dialog divs to not be visible
	
    //hide command field
	$("div#adminPanel #command").hide();	
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#adminPanel :input").addClass("ui-widget").attr("name",getId);
    
    //format buttons
    $("div#adminPanel :submit").button();
    
    $("div#adminPanel #pricelistInput").css("width","600px").attr("size", 62);
    $("div#adminPanel #invoiceInput").css("width","600px").attr("size", 62);
    $("div#adminPanel #scrapeInput").css("width","600px");
    $("div#adminPanel #uploadProducts").attr("value","Upload Pricelist");
    $("div#adminPanel #uploadInvoice").attr("value","Upload Order Invoice");
    $("div#adminPanel #scrape").attr("value","Scrape Website");    
    
    //set up form for ajax submit
    $("div#adminPanel #adminForm").submit(function(event) {event.preventDefault(); admin.executeAdminTask(event);});
};

admin.executeAdminTask = function (event) {
	var button = $("input[type=submit][clicked=true]");	
	button.removeAttr("clicked"); //remove clicked attribute of clicked button
	var id = button[0].id;	
	$("div#adminPanel #command").val(id);    
	if ($("div#adminPanel #"+id+"Input").val().length > 0) {	
		alert(event.target.id);
		$(event.target).ajaxSubmit({
			url: "admin",
			dataType: "json",
			cache: false,
			type: "post",                
			success: function( data ) {
				$("div#adminPanel #adminTableDiv").html(data.reduce(admin.extractTableData));	
			}
		});
		$("div#adminPanel #"+id+"Input").val("");
	} else {
		$("div#adminPanel #"+button+"Missing").dialog();
	}
	return false;
};

admin.extractTableData = function (string, currentValue) {//generate log table and add it to html string
	return (string+"<table border=1><caption>"+currentValue.logDescription+"</caption><tr>"+currentValue.headings.reduce(admin.extractHeaderData)+"</tr>"+currentValue.log.reduce(order.extractRowData)+"</tr>");
};

admin.extractRowData = function (string, currentValue) {//generate row of log table and add it to table string
		return (string+"<tr>"+currentValue.reduce(order.extractCellData)+"</tr>");
};

admin.extractCellData = function (string, currentValue) {//generate cell of log table row and add it to row string
	return (string+"<td>"+currentValue+"</td>");
};

admin.extractHeaderData = function (string, currentValue) {//generate cell of log table row and add it to row string
	return (string+"<th>"+currentValue+"</th>");
};
