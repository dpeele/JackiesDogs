var search = {};

search.onload = function () { //called onload of this panel

    $(window).resize(function() { //resize main body of form on window resize
        $("div#searchPanel #search").height($(window).height() - ($("div#searchPanel #search").offset().top + 75));
        $("div#searchPanel #leftSearch").height($(window).height() - ($("div#searchPanel #search").offset().top + 65));
        $("div#searchPanel #rightSearch").height($(window).height() - ($("div#searchPanel #search").offset().top + 65));
    });
    $(window).resize();	
	
    //dialogs
	$("div#searchPanel div.dialog").hide(); //set all dialog divs to not be visible
	
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#searchPanel :input").addClass("ui-widget").attr("name",getId);
    
    //set date fields to be datepicker ui elements and disable editing
    $("div#searchPanel .date").datepicker().attr("disabled","true"); 
    
    //check prechecked checkboxes
    $("div#searchPanel .checked").attr("checked","true");
    
    //select elements
    $("div#searchPanel #status").attr("multiple","multiple").attr("size",7); //set status listbox to height of 7 and allow multiple selection
    $("div#searchPanel #customer").attr("multiple","multiple").attr("size",10); //set customer listbox to height of 10 and allow multiple selection
    $("div#searchPanel #vendor").change(vendorOrder.toggleVendorOrderCreation());
    
    $("div#searchPanel form#customerOrderSearchForm :input").change(updateList);
    
	$("div#searchPanel #vendorOrderButton").button().attr("value","Generate").click(function () { //on click confirm and then create new vendor order based off of this list of orders		
		$("div#searchPanel #confirmVendorOrderCreateDialog").dialog({ 
			modal: true, 
			buttons: [ 
			    { text: "Continue", click:function() { 
			    	var queryString = $.makeArray($("div#searchPanel #orderDetails tr:gt(0)")).reduce(search.extractOrderId);
			    	queryString = queryString + "vendorTypeId=" + $("div#searchPanel #vendor").val();
			    	$(this).dialog("close");
			        $("#orderAnchor").attr("href",$("#vendorOrderAnchor").attr("href")+"?"+escape(queryString)); //set query string of vendor order entry screen url appropriately
			        $("#panels").tabs("option", "load", 2); //load the data into that panel
			        $("#panels").tabs("option", "select", 2); //select that panel			        
			}}, 	
			    { text: "Cancel", click:function() {        					 
			    	$(this).dialog( "close" ); 
		}}]});
	});
	
};

search.extractOrderId = function (string, currentValue) {//retrieve data from row of order table and add it to string
	return (string+"customerOrderId="+$(currentValue).find(":nth-child(1)").text()+"&");
};

search.toggleVendorOrderLookup = function () {
	if ($("div#searchPanel #vendor option:selected").length) { //an option has been selected
		$("div#searchPanel #vendorOrderButton").removeAttr("disabled");		
	} else { //otherwise no vendor has been selected so we disable vendor order creation button
		$("div#searchPanel #vendorOrderButton").attr("disabled", "disabled"); 
	}
};

search.addItem = function (id,customerName,orderDate,deliveryDate,cost,status) { //add item to order
	if ($("div#searchPanel #orderDetails tr").length == 1) {
		$("div#searchPanel #orderDetails tr").remove();
		$("div#searchPanel #orderDetails").append("<tr>\n" +
		    					"<th>ID</th>\n" +
	        	   				"<th>Customer Name</th>\n" +
		    	        		"<th>Order Date</th>\n" +
		    	        		"<th>Delivery Date</th>\n" +
		    	        		"<th>Total Price</th>\n" +
		    	        		"<th>Order Status</th>\n" +		    	        
			        			"</tr>\n"); //replace headers for order table
	}
	
	var rowValue = $('#orderDetails tr').length+1;//row number to add

	if (estimate) { //add indicator that this is an estimated price if applicable
		formattedtotalCost = formattedtotalCost + " (est)";
	}
	$("div#searchPanel #orderDetails tr:last").after("<tr>\n" + //add row
										"<td>"+ id +"</td>\n" +
										"<td>" + customerName + "</td>\n" +
										"<td>"+ orderDate +"</td>\n" +
										"<td>" + deliveryDate + "</td>\n" +
										"<td>" + cost +"</td>\n" +
										"<td>" + status + "</td>\n" +
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
									"</tr>\n");
	
	$("div#searchPanel #orderDetails tr:last").click(function () { //on click of row, confirm and then load that order into edit screen
		var id = $(this).find(":nth-child(1)").val(); 
		$("div#searchPanel #confirmOrderLoadDialog").dialog({ 
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

	$("div#searchPanel #button"+rowValue).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		$("div#searchPanel #orderDetails tr:last").click(function () { //on click of row, confirm and then remove row
			$("div#searchPanel #confirmOrderRemoveDialog").dialog({ 
				modal: true, 
				buttons: [ 
				    { text: "Continue", click:function() { 
				    	$(this).dialog("close");
						$(this).closest('tr').remove();  //hide row
						if ($("div#searchPanel #orderDetails tr").length == 1) {
							$("div#searchPanel #orderDetails tr").remove(); //no orders, only header- remove and 
							$("div#searchPanel #orderDetails").append("<tr>\n" +
							    					"<th colspan='6'>No orders match your search</th>\n" +		    	        
								        			"</tr>\n"); //replace headers for order table
						}						
					}}, 	
				    { text: "Cancel", click:function() {        					 
				    	$(this).dialog( "close" ); 
			}}]});
		});
	});//remove item from order
};

search.updateList = function () {
	$.ajax({
		url: "searchOrders",
		dataType: "json",
		cache: false,
		type: "post",                
		data: $("div#searchPanel #customerOrderSearchForm").serialize(),
		success: function( data ) {
			$.each( data.orders, function( item ) {                      	
				search.addItem(item.id, item.customer.fullName, item.orderDateFormatted, item.deliveryDateTimeFormatted, item.totalCost, item.status ); //add row to table for this order
				
			});
		}
	});
};
