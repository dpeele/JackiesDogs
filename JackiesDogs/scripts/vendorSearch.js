var vendorSearch = {};

vendorSearch.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("div#vendorSearchPanel #vendorSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 75));
        $("div#vendorSearchPanel #leftVendorSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 65));
        $("div#vendorSearchPanel #rightVendorSearch").height($(window).height() - ($("div#vendorSearchPanel #vendorSearch").offset().top + 65));
    });
    $(window).resize();	
	
    //dialogs
	$("div#vendorSearchPanel div.dialog").hide(); //set all dialog divs to not be visible
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#vendorSearchPanel :input").addClass("ui-widget").attr("name",getId);
    
    //set date fields to be datepicker ui elements and disable editing
    $("div#vendorSearchPanel .date").datepicker().attr("disabled","true"); 
    
    //check prechecked checkboxes
    $("div#vendorSearchPanel .checked").attr("checked","true");
    
    $("div#vendorSearchPanel #vendorStatus").attr("multiple","multiple").attr("size",7); //set status listbox to height of 7 and allow multiple selection
    $("div#vendorSearchPanel #vendor").attr("multiple","multiple").attr("size",1); //set vendor listbox to height of 1 and allow multiple selection
    
    $("div#vendorSearchPanel :input").change(vendorSearch.updateList);	
	
};

vendorSearch.addItem = function (id,orderDate,deliveryDate,cost,status, vendorName) { //add item to order
	if ($("div#vendorSearchPanel #vendorOrderDetails tr").length == 1) {
		$("div#vendorSearchPanel #vendorOrderDetails tr").remove();
		$("div#vendorSearchPanel #vendorOrderDetails").append("<tr>\n" +
		    					"<th>ID</th>\n" +
		    	        		"<th>Order Date</th>\n" +
		    	        		"<th>Delivery Date</th>\n" +
		    	        		"<th>Total Price</th>\n" +
		    	        		"<th>Order Status</th>\n" +
		    	        		"<th>Vendor</th>\n" +			    	        		
			        			"</tr>\n"); //replace headers for order table
	}
	
	var rowValue = $('#vendorOrderDetails tr').length+1;//row number to add
		
	var formattedCost = formatPrice(cost);

	if (estimate) { //add indicator that this is an estimated price if applicable
		formattedtotalCost = formattedtotalCost + " (est)";
	}
	$("div#vendorSearchPanel #vendorOrderDetails tr:last").after("<tr>\n" + //add row
										"<td>"+ id +"</td>\n" +
										"<td>"+ orderDate +"</td>\n" +
										"<td>" + deliveryDate + "</td>\n" +
										"<td>" + cost +"</td>\n" +
										"<td>" + status + "</td>\n" +
										"<td>" + vendorName + "</td>\n" +										
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
									"</tr>\n");
	
	$("div#vendorSearchPanel #vendorOrderDetails tr:last").click(function () { //on click of row, confirm and then load that order into edit screen
		var id = $(this).find(":nth-child(1)").val(); 
		$("div#vendorSearchPanel #confirmVendorOrderLoadDialog").dialog({ 
			modal: true, 
			buttons: [ 
			    { text: "Continue", click:function() { 
			    	$(this).dialog("close");		    	
			    	$("panels").tabs("url", "2", "loadVendorOrder?"+escape("vendorOrderId="+id)).tabs("option", "load", 2).tabs("option", "select", 2); //set url of order entry screen to appropriate id, load page into tab, and select tab
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
				        			"</tr>\n"); //replace headers for order table

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
				vendorSearch.addItem(item.id, item.orderDateFormatted, item.deliveryDateTimeFormatted, item.totalCost, item.status, item.vendor ); //add row to table for this order
				
			});
		}
	});
};
