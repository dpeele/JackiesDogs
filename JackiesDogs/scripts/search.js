var search = {};

search.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("#search").height($(window).height() - ($("#search").offset().top + 75));
        $("#leftSearch").height($(window).height() - ($("#search").offset().top + 65));
        $("#rightSearch").height($(window).height() - ($("#search").offset().top + 65));
    });
    $(window).resize();	
	
    //dialogs
	$("div.dialog").hide(); //set all dialog divs to not be visible
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $(":input").addClass("ui-widget").attr("name",$(this).attr("id"));
    
    //set date fields to be datepicker ui elements and disable editing
    $(".date").datepicker().attr("disabled","true"); 
    
    //check prechecked checkboxes
    $(".checked").attr("checked","true");
    
    $("#status").attr("multiple","multiple").attr("size",7); //set status listbox to height of 7 and allow multiple selection
    $("#customer").attr("multiple","multiple").attr("size",10); //set customer listbox to height of 10 and allow multiple selection
    
    $(":input").change(updateList)	
	
};

search.addItem = function (id,customerName,orderDate,deliveryDate,cost,status); { //add item to order
	if ($("#orderDetails tr").length == 1) {
		$("#orderDetails tr").remove();
		$("#orderDetails").append("<tr>\n" +
		    					"<th>ID</th>\n" +
	        	   				"<th>Customer Name</th>\n" +
		    	        		"<th>Order Date</th>\n" +
		    	        		"<th>Delivery Date</th>\n" +
		    	        		"<th>Total Price</th>\n" +
		    	        		"<th>Order Status</th>\n" +		    	        
			        			"</tr>\n"; //replace headers for order table
	}
	
	var rowValue = $('#orderDetails tr').length+1;//row number to add
		
	var formattedCost = formatPrice(cost);

	if (estimate) { //add indicator that this is an estimated price if applicable
		formattedtotalCost = formattedtotalCost + " (est)";
	}
	$("#orderDetails tr:last").after("<tr>\n" + //add row
										"<td>"+ id +"</td>\n" +
										"<td>" + customerName + "</td>\n" +
										"<td>"+ orderDate +"</td>\n" +
										"<td>" + deliveryDate + "</td>\n" +
										"<td>" + cost +"</td>\n" +
										"<td>" + status + "</td>\n" +
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
									"</tr>\n");
	
	$("#orderDetails tr:last").click(function () { //on click of row, confirm and then load that order into edit screen
		var id = $(this).find(":nth-child(1)").val(); 
		$("#confirmOrderLoadDialog").dialog({ 
			modal: true, 
			buttons: [ 
			    { text: "Continue", click:function() { 
			    	$(this).dialog("close");
			        $("#orderAnchor").attr("href",$("#orderAnchor").attr("href")+"?"+escape("orderId="+id)); //set url of order entry screen to appropriate id
			        $("#panels").tabs("option", "load", 0); //load the data into that panel
			        $("#panels").tabs("option", "select", 0); //select that panel			        
			}}, 	
			    { text: "Cancel", click:function() {        					 
			    	$(this).dialog( "close" ); 
		}}]});
	});

	$("#button"+rowValue).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		$(this).closest('tr').remove();  //hide row
		if ($("#orderDetails tr").length == 1) {
			$("#orderDetails tr").remove(); //no orders, only header- remove and 
			$("#orderDetails").append("<tr>\n" +
			    					"<th colspan='6'>No orders match your search</th>\n" +		    	        
				        			"</tr>\n"; //replace headers for order table

		}		
	});//remove item from order
};

search.updateList = function () {
	$.ajax({
		url: "searchOrders",
		dataType: "json",
		cache: false,
		type: "post",                
		data: $("#customerOrderSearchForm").serialize(),
		success: function( data ) {
			$.each( data.orders, function( item ) {                      	
				search.addItem(item.id, item.customer.fullName, item.orderDateFormatted, item.deliveryDateTimeFormatted, item.totalCost, item.status ); //add row to table for this order
				
			});
		}
	});
};
