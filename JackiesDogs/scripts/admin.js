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
    $("div#adminPanel #pricelist").attr("value","Upload Pricelist");
    $("div#adminPanel #invoice").attr("value","Upload Order Invoice");
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
		$(event.target).ajaxSubmit({
			url: "admin",
			dataType: "json",
			cache: false,
			type: "post",                
			success: function( data ) {
				if (data.uploadLogs.length > 0) {
					if (data.uploadLogs.length == 1) {
						$("div#adminPanel #adminTableDiv").html(admin.extractTableData("",data.uploadLogs[0]));
					} else {
						$("div#adminPanel #adminTableDiv").html(data.uploadLogs.reduce(admin.extractTableData));
					}
				} else {
					$("div#adminPanel #adminTableDiv").html("There are no errors to report.");
				}
			}
		});
		$("div#adminPanel #"+id+"Input").val("");
	} else {
		$("div#adminPanel #"+button+"Missing").dialog();
	}
	return false;
};
admin.extractTableData = function (string, currentValue) {//generate log table and add it to html string
	string = string + "<table style='width:100%;' border='1'><caption>"+currentValue.logDescription+"</caption><tr>";
	if (currentValue.headings.length > 0) {
		if (currentValue.headings.length == 1) {
			string = string + admin.extractHeaderData ("",currentValue.headings[0]);
		} else { 
			string = string + currentValue.headings.reduce(admin.extractHeaderData);
		}
	}
	string = string + "</tr>";
	if (currentValue.log.length > 0) {
		if (currentValue.log.length == 1) {
			string = string + admin.extractRowData("",currentValue.log[0]) + "</table>";
		} else {
			string = string + currentValue.log.reduce(admin.extractRowData) + "</table>";
		}
	}
	return (string);
};

admin.extractRowData = function (string, currentValue) {//generate row of log table and add it to table string
	if (currentValue.length > 0) {
		if (currentValue.length == 1) {
			return (string+"<tr>"+admin.extractCellData("", currentValue[0])+"</tr>");
		} else {
			return (string+"<tr>"+currentValue.reduce(admin.extractCellData)+"</tr>");
		}
	}
};

admin.extractCellData = function (string, currentValue) {//generate cell of log table row and add it to row string
	return (string+"<td>"+currentValue+"</td>");
};

admin.extractHeaderData = function (string, currentValue) {//generate cell of log table row and add it to row string
	return (string+"<th>"+currentValue+"</th>");
};
