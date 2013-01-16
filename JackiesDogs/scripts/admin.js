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
    
    //format buttons
    $(":submit").button();
    
    //set up form for ajax submit
    $("#adminForm").attr( "enctype", "multipart/form-data" ).attr( "encoding", "multipart/form-data" ).submit(admin.executeAdminTask);
};

admin.executeAdminTask = function () {
	var parameter;
	var div;
	var button = event.target;
    var buttonData = data + "&command=" + button.name
	if ($("#"+button+"Input").val().length > 0) {
		$.ajax({
			url: "admin",
			dataType: "json",
			cache: false,
			type: "post",                
			data: $("#adminForm").serialize()+buttonData,
			success: function( data ) {
				$("#adminTableDiv").html(data.reduce(order.extractTableData));	
			}
		});
	} else {
		$("#"+button+"Missing").dialog();
	}
};

admin.extractTableData = function (string) {//generate log table and add it to html string
	return (string+"<table border=1><caption>"+this.logDescription+"</caption><tr>"+this.headings.reduce(admin.extractHeaderData)+"</tr>"+this.log.reduce(order.extractRowData)+"</tr>");
};

admin.extractRowData = function (string) {//generate row of log table and add it to table string
		return (string+"<tr>"+this.reduce(order.extractCellData)+"</tr>");
};

admin.extractCellData = function (string) {//generate cell of log table row and add it to row string
	return (string"<td>"+this+"</td>");
};

admin.extractHeaderData = function (string) {//generate cell of log table row and add it to row string
	return (string"<th>"+this+"</th>");
};
