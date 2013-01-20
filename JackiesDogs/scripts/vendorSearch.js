var vendorSearch = {};

vendorSearch.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("div#vendorSearchPanel #vendorSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 75));
        $("div#vendorSearchPanel #leftSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 65));
        $("div#vendorSearchPanel #rightSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 65));
    });
    $(window).resize();	
	
    //dialogs
	$("div#vendorSearchPanel div.dialog").hide(); //set all dialog divs to not be visible
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#vendorSearchPanel :input").addClass("ui-widget").attr("name",$(this).attr("id"));
    
    //set date fields to be datepicker ui elements and disable editing
    $("div#vendorSearchPanel .date").datepicker().attr("disabled","true"); 
    
    //check prechecked checkboxes
    $("div#vendorSearchPanel .checked").attr("checked","true");
    
    $("div#vendorSearchPanel #vendorStatus").attr("multiple","multiple").attr("size",7); //set status listbox to height of 7 and allow multiple selection
    $("div#vendorSearchPanel #vendor").attr("multiple","multiple").attr("size",10); //set customer listbox to height of 10 and allow multiple selection
    
    $("div#vendorSearchPanel :input").change(updateList)	
	
};

vendorSearch.addItem = function (id,customerName,orderDate,deliveryDate,cost,status); { //add item to order
	if ($("div#vendorSearchPanel #vendorOrderDetails tr").length == 1) {
		$("div#vendorSearchPanel #vendorOrderDetails tr").remove();
		$("div#vendorSearchPanel #vendorOrderDetails").append("<tr>\n" +
		    					"<th>ID</th>\n" +
	        	   				"<th>Customer Name</th>\n" +
		    	        		"<th>Order Date</th>\n" +
		    	        		"<th>Delivery Date</th>\n" +
		    	        		"<th>Total Price</th>\n" +
		    	        		"<th>Order Status</th>\n" +		    	        
			        			"</tr>\n"; //replace headers for order table
	}
	
	var rowValue = $('#vendorOrderDetails tr').length+1;//row number to add
		
	var formattedCost = formatPrice(cost);

	if (estimate) { //add indicator that this is an estimated price if applicable
		formattedtotalCost = formattedtotalCost + " (est)";
	}
	$("div#vendorSearchPanel #vendorOrderDetails tr:last").after("<tr>\n" + //add row
										"<td>"+ id +"</td>\n" +
										"<td>" + customerName + "</td>\n" +
										"<td>"+ orderDate +"</td>\n" +
										"<td>" + deliveryDate + "</td>\n" +
										"<td>" + cost +"</td>\n" +
										"<td>" + status + "</td>\n" +
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
									"</tr>\n");
	
	$("div#vendorSearchPanel #vendorOrderDetails tr:last").click(function () { //on click of row, confirm and then load that order into edit screen
		var id = $(this).find(":nth-child(1)").val(); 
		$("div#vendorSearchPanel #confirmVendorOrderLoadDialog").dialog({ 
			modal: true, 
			buttons: [ 
			    { text: "Continue", click:function() { 
			    	$(this).dialog("close");
			        $("#vendorOrderAnchor").attr("href",$("#vendorOrderAnchor").attr("href")+"?"+escape("vendorOrderId="+id)); //set url of vendor order entry screen to appropriate id
			        $("#panels").tabs("option", "load", 2); //load the data into that panel
			        $(#panels").tabs("option", "select", 2); //select that panel			        
			}}, 	
			    { text: "Cancel", click:function() {        					 
			    	$(this).dialog( "close" ); 
		}}]});
	});

	$("div#vendorSearchPanel #button"+rowValue).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		$(this).closest('tr').remove();  //hide row
		if ($("div#vendorSearchPanel #vendorOrderDetails tr").length == 1) {
			$("div#vendorSearchPanel #vendorOrderDetails tr").remove(); //no orders, only header- remove and 
			$("div#vendorSearchPanel #vendorOrderDetails").append("<tr>\n" +
			    					"<th colspan='6'>No orders match your vendorSearch</th>\n" +		    	        
				        			"</tr>\n"; //replace headers for order table

		}		
	});//remove item from order
};

vendorSearch.updateList = function () {
	$.ajax({
		url: "searchVendorOrders",
		dataType: "json",
		cache: false,
		type: "post",                
		data: $("div#vendorSearchPanel #vendorOrderSearchForm").serialize(),
		success: function( data ) {
			$.each( data.orders, function( item ) {                      	
				vendorSearch.addItem(item.id, item.customer.fullName, item.orderDateFormatted, item.deliveryDateTimeFormatted, item.totalCost, item.status ); //add row to table for this order
				
			});
		}
	});
};
