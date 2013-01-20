var order = {};

order.blankCustomer = {
	label: "New Customer",
	value: "",
    firstName: "",
    lastName: "",
    streetAddress: "",
    aptAddress: "",
    city: "",
    state: "",
    zip: "",
    phone: "",
    email: "",
    custId: "0"};                                    

order.onload = function () { //called onload of this panel
	 
	order.defaultLabels(); //reset fieldset and panel labels
	
    $(window).resize(function() { //resize main body of form on window resize
        $("div#vendorOrderPanel #details").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 75));
        $("div#vendorOrderPanel #leftOrder").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 65));
        $("div#vendorOrderPanel #rightOrder").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 65));
    });
    $(window).resize();
    
    //customer information display div
    $("div#vendorOrderPanel #selectedCustomerDiv").addClass("ui-widget");
    
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#vendorOrderPanel :input").addClass("ui-widget").attr("name",$(this).attr("id"));
    
	$("div#vendorOrderPanel #customerLookup").simpletip({  
		content: "Please enter the first few letters or numbers of the first name, last name, " +
			"email, or phone number of an existing customer or select 'New Customer'",
		fixed: true
	});

	//hide hidden fields
	$("div#vendorOrderPanel .hidden").hide();
	
	//hide order item table since it's empty
	$("div#vendorOrderPanel #orderItems").hide();

	//set default value of order id to 0
	$("div#vendorOrderPanel #orderId").val("0");	

	//quantity field requires fixed width and handler for keyup
	$("div#vendorOrderPanel #quantity").css("width","75px").keyup(order.checkForShowAddButton); //when quantity value changes see whether we should show add button
	
	//fields that require fixed width
	$("div#vendorOrderPanel #quantityAvailable").css("width","75px");
	$("div#vendorOrderPanel #item").css("width","300px");        
    $("div#vendorOrderPanel #city").css("width","100px");
	$("div#vendorOrderPanel #email").css("width","200px");	    

	//fields with set max/min lengths
	$("div#vendorOrderPanel #phone").css("width","125px").attr("maxlength","14").attr("minlength","10");
    $("div#vendorOrderPanel #state").css("width","25px").attr("maxlength","2").attr("minlength","2");
    $("div#vendorOrderPanel #zip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    $("div#vendorOrderPanel #deliveryZip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    
    //recalculate final cost if any of the inputs are changed
    $("div#vendorOrderPanel .cost").change(order.setFinalCost);
    
    //dialogs
	$("div#vendorOrderPanel div.dialog").hide(); //set all dialog divs to not be visible
	
	//set value of delivered and personal checkboxes
	$("div#vendorOrderPanel :checkbox").attr("value","true");
    
	//buttons- all hidden initially
    $("div#vendorOrderPanel #addButton").button().attr("value","Add Item").click(order.addItem).hide(); //add item to order
    $("div#vendorOrderPanel #editCustomerButton").button().attr("value","Edit Customer").click(function () {order.popEditCustomer("Edit Customer");}).hide();//pop customer div
    $("div#vendorOrderPanel #submitButton").button().attr("value","Submit Order").click(order.validateAndSubmit).hide();//submit order
    $("div#vendorOrderPanel #cancelButton").button().attr("value","Cancel Order").click(order.confirmCancel).hide();//cancel order    
    $("div#vendorOrderPanel #enterDeliveryZipButton").button().attr("value","Calculate Delivery").click(order.popDelivery).hide();//pop delivery div
    
    //number only fields
    $("div#vendorOrderPanel .onlyNumbers").keydown(order.onlyNumbers).css("width","75px"); //only allow numbers
    
    //set discount field widths
    $("div#vendorOrderPanel #discount").css("width","25px");
    $("div#vendorOrderPanel #changeDue").css("width","50px");
    $("div#vendorOrderPanel #discount").css("width","25px");
    
    //decimal number only fields
    $("div#vendorOrderPanel .onlyDecimalNumbers").keydown(order.onlyNumbersAndDecimalPoint).css("width","75px"); //only allow numbers and a decimal point
            
    //set field widths
    $("div#vendorOrderPanel #quantity").css("width","35px");
    $("div#vendorOrderPanel #quantityAvailable").css("width","35px");
    $("div#vendorOrderPanel #discount").css("width","25px");
    $("div#vendorOrderPanel #changeDue").css("width","50px");
    $("div#vendorOrderPanel #credit").css("width","50px");
    $("div#vendorOrderPanel #deliveryFee").css("width","50px").attr("disabled","true");    
    $("div#vendorOrderPanel #tollExpense").css("width","50px").attr("disabled","true");        
    
    //date and time fields
    $("div#vendorOrderPanel #deliveryDate").datepicker().css("width","100px").keyup(order.checkForDeliveryButton); //create date picker for optional delivery date
    $("div#vendorOrderPanel #deliveryTime").timePicker({startTime: "07:00",endTime: "21:00",show24Hours: false,step: 15}).css("width","75px").keyup(order.checkForDeliveryButton); //create time picker for optional delivery time
   
    
    //display only fields
    $("div#vendorOrderPanel .readOnly").attr("readonly",true);//don't allow editing of total price or quantity available

    //set default value for display only total fields
    $("div#vendorOrderPanel .total").val("$0.00").css("width","75px");    
    
    //customer autocomplete
    $("div#vendorOrderPanel #customerLookup").autocomplete({ //look up client based on last name
        source: function( request, response ) {
        	if (request.term.length < 2) {
            	response([order.blankCustomer]);                             	
			} else {
                $.ajax({
                    url: "customerLookup",
                    dataType: "json",
                    cache: false,
                    type: "post",                
                    data: {
                        maxRows: 12,
                        match: request.term
                    },
                    success: function( data ) {
                    	var responseArray = $.map( data.customers, function( item ) {                      	
                           	var additionalInfo = " (";
                           	var divider = "";
                           	if (item.phone.length != 0) {
                           		additionalInfo = additionalInfo + "phone: " + item.phone;
                           		divider = ", ";
                           	}
                           	if (item.email.length != 0) {
                           		additionalInfo = additionalInfo + divider + "email: " + item.email;
                           	}
                           	additionalInfo = additionalInfo + ")";
                           	return ({                            		
                               	label: item.lastName+ ", " + item.firstName + additionalInfo,
                               	value: "",
                               	firstName: item.firstName,
                               	lastName: item.lastName,
                               	streetAddress: item.streetAddress,
                               	aptAddress: item.aptAddress,
                               	city: item.city,
                               	state: item.state,
                               	zip: item.zip,
                               	phone: item.phone,
                               	email: item.email,
                               	custId: item.id
                           	});
                        });
                    	responseArray.push(order.blankCustomer)
                        response(responseArray);
                    }
                });
        	}
        },    	
        select: function (event, ui) {
        	order.populateCustomer(ui.item);
        	if (ui.item.custId == "0") { //new user has been selected, pop up empty customer edit dialog
        		order.popEditCustomer("Enter Customer");
			} else {
				order.populateCustomerDiv(); 
				$("div#vendorOrderPanel #editCustomerButton").show();//show edit customer button
				order.checkForShowSubmitButton();//see whether we should show submit button
				order.checkForShowCancelButton(); //see whether we should show cancel button
			}
        },        
        minLength: 0
    }).bind("focus",function(){$(this).trigger('keydown.autocomplete');});//force autocomplete to open with just new customer entry on focus 

    //product autocomplete
    $( "#item" ).autocomplete({ //look up product based on name or id
        source: function( request, response ) {
            $.ajax({
                url: "productLookup",                
                dataType: "json",
                cache: false,
                type: "post",
                data: {
                    maxRows: 12,
                    match: request.term
                },
                success: function( data ) {
                    response( $.map( data.products, function( item ) {
                    	var categories = "";
                    	if (item.categories.length() > 0) {
                    		for (var i=0; i<item.categories.length(); i++) {
                    			categories = categories + item.categories[i] + ", ";
                    		}
                    		categories = categories.substring(0, categories.length-2) + "; ";
                    	}
                       	return {
                           	label: item.productName + "(" + categories + item.billBy + ")",
                           	value: "",                       		
                    		id: item.id,
                    		productName: item.productName + "(" + item.billBy + ")",
                    		description: item.description,
                    		quantity: item.inventory.quantity,
                    		price: item.price,
                    		billBy: item.billBy, 
                    		estimatedWeight: item.estimatedWeight,    
                       	};
                    }));
                }
            });
        },    	
        focus: function( event, ui ) {
        	if (ui.item.description.length > 0) {
        		$(this).simpletip({
        			content: ui.item.description,
					fixed: true
				}).show();
        	}
        },
        select: function (event, ui) {
        	$("div#vendorOrderPanel #itemId").val(ui.item.id);
        	$("div#vendorOrderPanel #price").val(ui.item.price);
        	$("div#vendorOrderPanel #quantityAvailable").val(ui.item.quantity);
        	$("div#vendorOrderPanel #item").val(ui.item.productName);
    		$("div#vendorOrderPanel #billBy").val(ui.item.billBy); 
    		$("div#vendorOrderPanel #estimatedWeight").val(ui.item.estimatedWeight);             
    		$("div#vendorOrderPanel #description").val(ui.item.description);
    		order.checkForShowAddButton();//see whether we should show add button
        },        
        minLength: 2,
    }); 

};

order.defaultLabels = function () {
	$("#vendorOrderAnchor").html("New Customer Order"); //default vendor order panel label is New Vendor Order
	$("div#vendorOrderPanel #orderLegend").html("Enter Information:"); //default legend is Enter Information:
	 $("#vendorOrderAnchor").attr("href","loadVendorOrder"); //dafault url for panel is loadOrder
};

order.checkForShowSubmitButton = function () {
	if (($("div#vendorOrderPanel #selectedCustomerDiv").html().length > 0) && ($('#orderItems tr').length > 1)) {
		$("div#vendorOrderPanel #submitButton").show();
	}
};

order.checkForShowCancelButton = function () {
	if (($("div#vendorOrderPanel #selectedCustomerDiv").html().length > 0) || ($('#orderItems tr').length > 1)) {
		$("div#vendorOrderPanel #cancelButton").show();
	}
};

order.checkForShowAddButton = function () {
	if (($("div#vendorOrderPanel #itemId").val().length > 0) && ($("div#vendorOrderPanel #quantity").val().length > 0)) {
		$("div#vendorOrderPanel #addButton").show();
	}
};

order.setFinalCost = function () {
	var totalCost = $("div#vendorOrderPanel #totalCost").val();
	var finalCost = totalCost - ((totalCost / 100) * $("div#vendorOrderPanel #discount").val());  //subtract food discount from order total
	finalCost = finalCost - $("div#vendorOrderPanel #credit").val() + $("div#vendorOrderPanel #deliveryFee").val() + $("div#vendorOrderPanel #tollExpense").val(); //add other costs and subtract credit from order total	
	$("div#vendorOrderPanel #finalCost").val(finalCost);
}

order.setFloatValue = function (value, name) { //if the float value isn't zero, set field name to formatted value
	if (parseFloat(value != 0)) {
		$("div#vendorOrderPanel #"+name).val(formatPrice(value).substring(1));
	}
}

order.setValues = function (id,deliveryDate,deliveryTime,discount,credit,deliveryFee,tollExpense,totalCost,status,changeDue,delivered,personal) {
	var totalFoodCost = totalCost + ((totalCost / 100) * discount);  //add food discount to order total
	totalFoodCost = totalCost + credit - deliveryFee - tollExpense; //subtract other costs and add credit to order total to give us the total of just the food
	$("div#vendorOrderPanel #totalCost").val(totalFoodCost); //set total food cost	
	if (parseInt(credit) != 0) { //if there is a credit
		$("div#vendorOrderPanel #credit").val(credit);
	}
	order.setFloatValue(discount,"discount"); //set the double values
	order.setFloatValue(deliveryFee,"deliveryFee");
	order.setFloatValue(tollExpense,"tollExpense");
	order.setFloatValue(changeDue,"changeDue");
	$("div#vendorOrderPanel select option[value='"+status+"']").attr("selected","selected"); //select the correct status option
	$("div#vendorOrderPanel #orderId").val(id); //set order id
	$("div#vendorOrderPanel #deliveryDate").val(deliveryDate); //set delivery date and time
	$("div#vendorOrderPanel #deliveryTime").val(deliveryTime);
	if (delivered == "true") { //if delivered is true check delivered checkbox
		$("div#vendorOrderPanel #delivered").attr("value","true");
	}
	if (personal == "true") { //if personal is true check personal checkbox
		$("div#vendorOrderPanel #personal").attr("value","true");
	}	
	
	//show buttons where appropriate
	order.checkForShowSubmitButton();
	order.checkForShowCancelButton();		
	order.checkForDeliveryButton();
	$("div#vendorOrderPanel #editCustomerButton").show();
	$("div#vendorOrderPanel #orderAnchor").html("Edit Customer Order"); //change order panel label to Edit Order 
	$("div#vendorOrderPanel #orderLegend").html("Edit Information:"); //change legend to Edit Information:	
};

order.checkForDeliveryButton = function () {
	if (($("div#vendorOrderPanel #deliveryDate").val().length > 0) && ($("div#vendorOrderPanel deliveryTime").val().length > 0)) {
		$("div#vendorOrderPanel #enterDeliveryZipButton").show();
        $("div#vendorOrderPanel #deliveryFee").attr("disabled","false");    
        $("div#vendorOrderPanel #tollExpense").attr("disabled","false");  
	}	
};

order.checkQuantity = function () { //check quantity ordered vs quantity in stock and confirm if quantity ordered is greater
	var quantityCheck =$("div#vendorOrderPanel #quantityAvailable").val() -$("div#vendorOrderPanel #quantity").val(); //make sure you aren't adding more than we have
	if (quantityCheck < 0) {
		$("div#vendorOrderPanel #quantityOverrideDialog").dialog({ 
			modal: true, 
			buttons: [ 
				{ text: "Continue", click:function() { 
					$(this).dialog("close");
					order.addItem();									
				}}, 	
				{ text: "Cancel", click:function() {        					 
					$(this).dialog( "close" ); 
		}}]});		
	} else {
		order.addItem();
	}
};

order.addItem = function (id, quantity, weight, itemId, name, price, billBy, estimatedWeight, description) { //add item to order
	if ($("div#vendorOrderPanel #orderItems tr").length == 1) {
		$("div#vendorOrderPanel #orderItems").show();
	}
	var totalItemPrice;
	var estimate;
	var byThePound;
	if (arguments.length == 0) {//not an existing item
		id=0;
		billBy=$("div#vendorOrderPanel #billBy").val();
		quantity=$("div#vendorOrderPanel #quantity").val();
		price=$("div#vendorOrderPanel #price").val();
		estimatedWeight=$("div#vendorOrderPanel #estimatedWeight").val();
		itemId=$("div#vendorOrderPanel #itemId").val();
		name=$("div#vendorOrderPanel #item").val();
		description=$("div#vendorOrderPanel #description").val();
	}
	
	if (billBy == "Pound") {//if price is per pound, estimate weight and use this to determine estimated total price  
		byThePound = true;
		if (parseFloat(weight) == 0) { //no exact weight, use estimatedWeight		
			estimate=true;
			totalItemPrice = quantity*price*estimatedWeight;
		} else { //exact weight is available 
			totalItemPrice = price*weight;
		}
	} else {
		totalItemPrice = quantity*price; //current total price
	}

	var rowValue = $('#orderItems tr').length+1;//row number to add
		
	var formattedtotalCost = formatPrice(totalItemPrice);

	if (estimate) { //add indicator that this is an estimated price if applicable
		formattedtotalCost = formattedtotalCost + " (est)";
	}
	$("div#vendorOrderPanel #orderItems tr:last").after("<tr>\n" + //add row
										"<td>"+itemId+"</td>\n" +
										"<td id='dbIdTd"+rowValue+"'><input type='text'/></td>\n" +
										"<td>"+name+"</td>\n" +
										"<td><input type='text' id='quantity"+rowValue+"'/></td>\n" +
										"<td>" + formatPrice(price)+"</td>\n" +
										"<td id='totalItemPrice"+rowValue+"'>" + formattedtotalCost + "</td>\n" +
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
										"<td id='removedTd"+rowValue+"'><input type='text' id='removed"+rowValue+"'/></td>\n" +
									"</tr>\n");
		
	$("div#vendorOrderPanel #removedTd"+rowValue).hide(); //hide td holding field to indicate whether or not row has been hidden
	$("div#vendorOrderPanel #")
	$("div#vendorOrderPanel #dbIdTd"+rowValue).hide().find(":text").val(id); //hide td holding field and set dbId inside
	$("div#vendorOrderPanel #totalItemPriceUnformatted"+rowValue).css("display","none"); //hide field to store unformatted total price
		
	if (description.length > 0) { //add tooltip to table row if description exists
		$("div#vendorOrderPanel #orderItems tr:last").simpletip({  
			content: description,
			fixed: true
		});
	}	
		
	if (byThePound ) { //priced by the pound, add focus event handler to pop up estimated dialog and handle that
		if (estimate) {
			$("div#vendorOrderPanel #quantity"+rowValue).val(quantity+" (" + quantity*estimatedWeight + "lbs)");//estimate weight from quantity
		} else {
			$("div#vendorOrderPanel #quantity"+rowValue).val(quantity+" (" + weight + "lbs)");//use exact weight
		}
		$("div#vendorOrderPanel #quantity"+rowValue).focus(function() {
			var exactQuantity = $("div#vendorOrderPanel #quantity"+rowValue).val();
			var exactWeight = "";
			var start = quantity.indexOf(" ("); //location of front parenthesis if it exists
			if (start != -1) { //front parenthesis exists and we have an exact weight for this item
				exactWeight = exactQuantity.substring(start+2, exactQuantity.indexOf("lbs)")); //strip out weight
				exactQuantity = exactQuantity.substring(0,start); //strip out quantity	
			}
			$("div#vendorOrderPanel #exactQuantity").val(exactQuantity);//set initial quantity value in dialog to value from table row
			$("div#vendorOrderPanel #exactWeight").val(exactWeight);//set initial weight value in dialog to value from table row
			$("div#vendorOrderPanel #editPoundQuantityDialog").dialog({ 
				modal: true, 
				title: "Enter Exact Weight",
				width: 400,
				buttons: [ 
					{ text: "Submit", click:function() { 
						if (!($("div#vendorOrderPanel #exactQuantityForm").validate().form()) || $("div#vendorOrderPanel #exactWeight").val()==".") {//validate form
							$("div#vendorOrderPanel #incompletePoundQuantityDialog").dialog({ modal: true });
							return;	
						}																			
						$("div#vendorOrderPanel #quantity"+rowValue).val($("div#vendorOrderPanel #exactQuantity").val()+" (" + $("div#vendorOrderPanel #exactWeight").val() + "lbs)");
						var exacttotalCost= formatPrice(parseFloat($("div#vendorOrderPanel #exactWeight").val()) * $("div#vendorOrderPanel #price").val());
						var exacttotalCostNumeric = exacttotalCost.substring(1);
						var totalItemPriceNumeric;
						var index = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().indexOf(" (est)");
						if (index == -1) {
							totalItemPriceNumeric = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().substring(1);
						} else {
							totalItemPriceNumeric = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().substring(1,index);
						}
						$("div#vendorOrderPanel #totalCost").val(formatPrice(parseFloat($("div#vendorOrderPanel #totalCost").val().substring(1))-totalItemPriceNumeric+exacttotalCostNumeric));//update total
						$("div#vendorOrderPanel #totalItemPrice"+rowValue).html(exacttotalCost);
						$("div#vendorOrderPanel #totalItemPriceUnformatted"+rowValue).val(exacttotalCostNumeric);
						$(this).dialog("close");
					}}, 	
						{ 
							text: "Cancel", 
							click:function() {        					 
								$(this).dialog("close"); 
			}}]});
		});	
	} else { //not estimate, add keydown handler and adjust price as they type
		$("div#vendorOrderPanel #quantity"+rowValue).val(quantity).css("width","100px"); //set quantity
		$("div#vendorOrderPanel #quantity"+rowValue).keydown(function(event){ //add keydown event handler to quantity text box to allow 
														 //only numbers and a decimal point and update price on quantity change			
			if(order.onlyNumbers(event)) {//value may have changed, update total price
				var totalItemPriceNumeric = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().substring(1);			
				if ($("div#vendorOrderPanel #quantity"+rowValue).val().length == 0) { //no quantity
					$("div#vendorOrderPanel #totalCost").val(formatPrice(parseFloat($("div#vendorOrderPanel #totalCost").val().substring(1))-totalItemPriceNumeric));//update total
					$("div#vendorOrderPanel #totalItemPrice"+rowValue).html("$0.00");
				} else {//handle new >0 quantity
					var exacttotalCost= formatPrice(parseFloat($("div#vendorOrderPanel #quantity"+rowValue).val()) * $("div#vendorOrderPanel #price").val());
					var exacttotalCostNumeric = exacttotalCost.substring(1);						
					$("div#vendorOrderPanel #totalCost").val(formatPrice(parseFloat($("div#vendorOrderPanel #totalCost").val().substring(1))-totalItemPriceNumeric+exacttotalCostNumeric));//update total
					$("div#vendorOrderPanel #totalItemPrice"+rowValue).html(exacttotalCost);					
				}
			}
		});	
	}
				            			
	$("div#vendorOrderPanel #button"+rowValue).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		var totalItemPriceNumeric;
		var index = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().indexOf(" (est)");
		if (index == -1) {
			totalItemPriceNumeric = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().substring(1);
		} else {
			totalItemPriceNumeric = $("div#vendorOrderPanel #totalItemPrice"+rowValue).html().substring(1,index);
		}
		$("div#vendorOrderPanel #totalCost").val(formatPrice(parseFloat($("div#vendorOrderPanel #totalCost").val().substring(1))-totalItemPriceNumeric));//update total
		$(this).closest('tr').hide();  //remove row
		$("div#vendorOrderPanel #removed"+rowValue).val("true");
		if ($("div#vendorOrderPanel #orderItems tr").length == 1) {
			$("div#vendorOrderPanel #orderItems").hide();
		}		
	});//remove item from order

	$("div#vendorOrderPanel .item").val(""); //reset item fields
	$("div#vendorOrderPanel #addButton").hide();
	order.checkForShowSubmitButton(); //see whether we should show submit button
	order.checkForShowCancelButton(); //see whether we should show cancel button
	if ($("div#vendorOrderPanel #totalCost").val() != "") { // update total price
		$("div#vendorOrderPanel #totalCost").val(formatPrice(parseFloat($("div#vendorOrderPanel #totalCost").val().substring(1))+totalItemPrice));
	} else {
		$("div#vendorOrderPanel #totalCost").val(formatPrice(totalItemPrice));
	}
};

order.populateCustomer = function(item) { //populate customer dialog fields from json object
	$("div#vendorOrderPanel #firstName").val(item.firstName);
	$("div#vendorOrderPanel #lastName").val(item.lastName);
    $("div#vendorOrderPanel #streetAddress").val(item.streetAddress);
 	$("div#vendorOrderPanel #aptAddress").val(item.aptAddress);    
    $("div#vendorOrderPanel #phone").val(item.phone);
    $("div#vendorOrderPanel #state").val(item.state);
    $("div#vendorOrderPanel #city").val(item.city);
    $("div#vendorOrderPanel #zip").val(item.zip);
    $("div#vendorOrderPanel #email").val(item.email);
    $("div#vendorOrderPanel #custId").val(item.custId);  
    $("div#vendorOrderPanel #editCustId").val(item.custId);        		   
};

order.addInfo = function (customerInfo, i) {
	var value = $(this).val();
	if (value.length != 0) {
		customerInfo = customerInfo + value;
	}
	if (i == 4) {
		customerInfo = customerInfo + ", ";
	} else if (i==5) {
		customerInfo = customerInfo + " ";
	} else {
		customerInfo = customerInfo = "<br/>"
	}
	return (customerInfo);
};

order.populateCustomerDiv = function() { //populate customer div from customer dialog fields
	var customerInfo = $("div#vendorOrderPanel #firstName").val() + " " + $("div#vendorOrderPanel #lastName").val() + "<br/>";
	var additionalInfo = "";
	if ($("div#vendorOrderPanel #phone").val().length != 0) {
   		additionalInfo = additionalInfo + "phone: " + $("div#vendorOrderPanel #phone").val() + ", ";
	}
	if ($("div#vendorOrderPanel #email").val().length != 0) {
   		additionalInfo = additionalInfo + "email: " + $("div#vendorOrderPanel #email").val() + "<br/>";
	} else {
		additionalInfo = additionalInfo.substring(0,additionalInfo.length-2) + "<br/>"//remove ", " and add line break if there isn't an email address 
	}
	customerInfo = $("div#vendorOrderPanel .customerAddress").reduce(customerInfo,addInfo); //reduce address fields to single piece of HTML
	$("div#vendorOrderPanel #selectedCustomerDiv").html(customerInfo+additionalInfo); //populate customer div with customer information
};

order.popDelivery = function () { //pop delivery calculator div
	$("div#vendorOrderPanel #deliveryZip").val($("div#vendorOrderPanel #zip").val()); //prepopulate with customer's zip
	$("div#vendorOrderPanel #peak").attr("checked",false);
	$("div#vendorOrderPanel #enterDeliveryZipDialog").dialog({ 
		modal: true, 
		title: "Enter Delivery Zip to Calculate Fee and Tolls",
		width: 400,
		buttons: [ 
			{ text: "Submit", click:function() { 
				if (!($("div#vendorOrderPanel #enterDeliveryZipForm").validate().form())) {//validate form
					$("div#vendorOrderPanel #incompleteDeliveryDialog").dialog({ modal: true });
					return;	
				}			
				order.requestDestinationData($("div#vendorOrderPanel #deliveryZip").val());
				$(this).dialog("close"); 
			}}, 	
				{ 
					text: "Cancel", 
					click:function() {        					 
						$(this).dialog("close"); 
	}}]});	
};

order.popEditCustomer = function (dialogTitle) { //pop up customer edit dialog box
	$("div#vendorOrderPanel #customerDialog").dialog({ 
		modal: true,
		title: dialogTitle,
		width: 500,
		buttons: [ { 
			text: "Save", 
			click: function() { 
				$("div#vendorOrderPanel #confirmSaveDialog").dialog({ 
					modal: true, 
					buttons: [ 
						{ text: "Yes", click:function() { 
							$(this).dialog("close");
							order.validateAndSubmitCustomer();									
						}}, 	
							{ text: "No", click:function() {        					 
							$(this).dialog("close"); 
				}}]});
		}}, 	
		{
			text: "Cancel", 
			click: function() {
    			$("div#vendorOrderPanel #confirmCancelDialog").dialog({ 
        			modal: true, 
        			buttons: [ 
        				{ 
        					text: "Yes", 
        					click: function() { 
        						$(this).dialog("close");
								$("div#vendorOrderPanel #customerDialog").dialog("close");
	        			}}, 	
    	    			{ 
        					text: "No", 
        					click: function() {        					 
        						$(this).dialog("close"); 
	        	}}]});
		}},
		{
			text: "Clear", 
			click: function() {
	    		$("div#vendorOrderPanel #confirmClearDialog").dialog(
	    			{ 
    	    			modal: true, 
        				buttons: [ 
        					{ 
        						text: "Yes", 
        						click: function() {
									$(this).dialog("close"); 
        							order.clearCustomer();
	        				}}, 	
    	    				{ 
        						text: "No", 
        						click: function() {        					 
        							$(this).dialog("close"); 
	        	}}]});
		}}]});	
		
};

order.clearCustomer = function () { //clear customer data
	$("div#vendorOrderPanel #customerForm input[type=text]").val("");
};

order.validateAndSubmitCustomer = function () { //validate and submit customer dialog via ajax
	if ((!($("div#vendorOrderPanel #customerForm").validate().form())) || (($("div#vendorOrderPanel #email").val().length == 0) && ($("div#vendorOrderPanel #phone").val().length == 0))) {//validate form
		$("div#vendorOrderPanel #incompleteCustomerDialog").dialog({ modal: true });
		return;	
	}
    $.ajax({ //make ajax call to submit order
        url: "customerUpdate",
        cache: false,
        type: "post",  
        dataType: "json",      
        data: $("div#vendorOrderPanel #customerForm").serialize(),
        success: function( data ) { //order submission succeeded
        	order.populateCustomerDiv();
        	$("div#vendorOrderPanel #custId").val(data.custId); //populate customer id on order form with correct customer id
        	$("div#vendorOrderPanel #editCustomerButton").show();//show edit customer button
        	order.checkForShowSubmitButton();//see whether we should show submit button
        	order.checkForShowCancelButton(); //see whether we should show cancel button
        },
        error: function () { //order submission failed
        	$("div#vendorOrderPanel #customerSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });	
	$("div#vendorOrderPanel #customerDialog").dialog("close");
};

order.validateAndSubmit = function () { //validate form data and ajax submit to server
	$("div#vendorOrderPanel #orderInfo").val($("div#vendorOrderPanel #orderItems tr:gt(0)").reduce("",order.extractItemData));//put table data into hidden field to be sent to server using reduce and function to pull each row's data
	
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("div#vendorOrderPanel #orderForm").serialize(),
        success: function( data ) { //order submission succeeded
        	$("div#vendorOrderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been submitted. Estimated cost (minus any estimated/unavailable weights) is " + formatPrice(totalCost)+".").dialog({ modal: true }); //pop success dialog
        	$("div#vendorOrderPanel #orderId").val(data.orderId); //set order id
        },
        error: function () { //order submission failed
        	$("div#vendorOrderPanel #orderSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

order.resetForm = function () {
	$("div#vendorOrderPanel #orderItems tr:gt(0)").remove(); //empty order table except for header
	$("div#vendorOrderPanel input[type=text]").val(""); //empty all fields
	$("div#vendorOrderPanel #submitButton").hide();
	$("div#vendorOrderPanel #editCustomerButton").hide();        	
	$("div#vendorOrderPanel #selectedCustomerDiv").html("");
	//hide buttons
    $("div#vendorOrderPanel #addButton").hide();
    $("div#vendorOrderPanel #editCustomerButton").hide();
    $("div#vendorOrderPanel #submitButton").button().hide();
    $("div#vendorOrderPanel #enterDeliveryZipButton").hide();
    //disable delivery fields
    $("div#vendorOrderPanel #deliveryFee").attr("disabled","true");    
    $("div#vendorOrderPanel #tollExpense").attr("disabled","true");    
    $("div#vendorOrderPanel #orderId").val("0")   //set orderId to blank order
    order.defaultLabels(); //reset fieldset and panel labels
    $("div#vendorOrderPanel #orderItems").hide(); //hide order item table since it's now empty
};

order.confirmCancel = function () { //confirm order cancelation
		$("div#vendorOrderPanel #confirmCancelDialog").dialog({ 
			modal: true, 
			buttons: [ 
				{ text: "Yes", click:function() { 
					$(this).dialog("close");
					order.cancelOrder();									
				}}, 	
					{ text: "No", click:function() {        					 
					$(this).dialog("close"); 
		}}]});	
};

order.cancelOrder = function () { //cancel order- submit to server if it is an existing order that must be updated in the database
	if ($("div#vendorOrderPanel #orderId").val() == "0") {
		order.resetForm();
	}
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("div#vendorOrderPanel #orderForm").serialize()+"&cancelled=" + encodeURIComponent("Cancelled"),//append cancelled parameter to form data
        success: function( data ) { //order submission succeeded
        	$("div#vendorOrderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been cancelled.").dialog({ modal: true }); //pop successful cancellation dialog
    		order.resetForm();        	
        },
        error: function () { //order submission failed
        	$("div#vendorOrderPanel #orderCancellationFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

order.extractItemData = function (string) {//retrieve data from row of order table and add it to string
	if ($(this).find(":nth-child(2)").val() == "0" && $(this).find(":nth-child(8)").val() == "true") {
		return;
	} else {
		return (string+"id="+$(this).find(":nth-child(1)").text()+"#quantity="+$(this).find(":nth-child(4)").find(":text").val()
				+"#dbId="+$(this).find(":nth-child(2)").find(":text").val()+"#removed="+$(this).find(":nth-child(8)").find(":text").val()+">");
	}
};

order.onlyNumbersAndDecimalPoint = function (event) { //only allow numbers to be entered in this text input
    if ( event.keyCode == 190 ) { //if the user pressed a decimal point
    	if ($("div#vendorOrderPanel #"+event.target.id).val().indexOf(".") == -1) { //if we don't already have a decimal point in the number then allow it
			return true; //this may have affected value- changed 1 to .1, etc     	
    	} else { //we already have a decimal point, cancel keypress and return
    		event.preventDefault();
    		return false;
    	}
    } else if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13) {
    	return true; // Allow: backspace, delete, tab, escape, and enter, return true since this likely changed value
    } else if ((event.keyCode == 65 && event.ctrlKey === true) || // Allow: Ctrl+A 
       (event.keyCode >= 35 && event.keyCode <= 39)) { // Allow: home, end, left, right
    	return false; // let it happen, don't do anything, return false             
    } else if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
    	event.preventDefault(); // Ensure that it isn't a number and stop the keypress
    	return false; 
    } else { //number
    	return true;
    }
};

order.onlyNumbers = function (event) { //only allow numbers to be entered in this text input
    if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13) {
    	return true; // Allow: backspace, delete, tab, escape, and enter, return true since this likely changed value
    } else if ((event.keyCode == 65 && event.ctrlKey === true) || // Allow: Ctrl+A 
       (event.keyCode >= 35 && event.keyCode <= 39)) { // Allow: home, end, left, right
        return false; // let it happen, don't do anything, return false             
    } else if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
    	event.preventDefault(); // Ensure that it isn't a number and stop the keypress
    	return false; 
    } else { //number
    	return true;
    }
};

order.requestDestinationData = function (zip) {
	// Take the provided zip, and add it to a mapquestapi query. Make sure you encode it!
	var address = 'http://www.mapquestapi.com/directions/v1/routematrix?format=json&key=Fmjtd%7Cluuan10al9%2C8s%3Do5-968x9y&json={locations:[%2211385%22,%22'+zip+'%22],options:%20{allToAll:%20false}}';
    $.ajax({ //make ajax call to submit query
        url: address,
        cache: false,
        type: "get",
        dataType: "jsonp",
        success: function(data) {
        	populateDeliveryInfo({distance: data.distance[1],county: data.locations[1].adminArea4,state: data.locations[1].adminArea3});//call function to calculate delivery info
        }
    });	
};

order.adjustHour = function (hour, period) { //convert hour to 24 hour clock
	if (period == "PM") {
		return (parseInt(hour) + 12);
	} else {
		return (hour);
	}
};

order.splitTime = function (time) { //split time into hours and minutes with period attached
	return time.split(":");
};

order.splitMinute = function (minutes) { //split minutes and period
	return minutes.split(" ");
};

order.compareTime = function (time1, time2) { //get difference in seconds betwee two times
	   var splitTime1 = order.splitTime(time1); //split 1st time
	   var splitTime2 = order.splitTime(time2); //split 2nd time
	   var splitMinute1 = order.splitMinute(splitTime1[1]); //split AM/PM from minute for 1st time 
	   var splitMinute2 = order.splitMinute(splitTime2[1]); //aplit AM/PM from minute for 2nd time
	   var minute1 = splitMinute1[0];
	   var minute2 = splitMinute2[0];
	   var hour1 = order.adjustHour(splitTime1[0], splitMinute1[1]); //convert hour for 1st time to 24 hour clock
	   var hour2 = order.adjustHour(splitTime2[0], splitMinute2[1]); //convert hour for 2nd time to 24 hour clock
	   var seconds1 = hour1 * 3600 + minute1 * 60; //convert time1 to seconds
	   var seconds2 = hour2 * 3600 + minute1 * 60; //convert time2 to seconds
	   return (seconds1 - seconds2); // Gets difference in seconds
	}

order.populateDeliveryInfo = function (destinationInfo) {
	if (destinationInfo.state == "NJ") { //determine delivery fee and toll based on distance, county, and state
		//determine if this a peak time as tolls are different for peak and off-peak
		var dateArray = $("div#vendorOrderPanel #deliveryDate").val().split("/"); //split date from deliveryDate field
		var dayOfWeek = new Date(dateArray[2], dateArray[0]-1, dateArray[1]).getDay();
		var deliveryTime = parseInt($("div#vendorOrderPanel #deliveryTime").val()); //get time from deliveryTime field
		var peak = false;
		if (dayOfWeek == 0 || dayOfWeek == 6) { //this is a weekend date 
			if ((order.compareTime(deliveryTime,"10:00 AM")>0) && (order.compareTime("9:00 PM",deliveryTime)>0)) { // if delivery time is after 10AM in the morning and before 9PM in the evening
				peak = true;
			}
		} else /*weekday*/ if (((order.compareTime(deliveryTime,"5:00 AM")>0) && (order.compareTime("10:00 AM",deliveryTime)>0)) || // if delivery time is between 5AM and 10AM in the morning 
					(order.compareTime(deliveryTime,"3:00 PM")>0) && (order.compareTime("8:00 PM",deliveryTime)>0)) { // or if delivery time is between 3PM and 8PM in the evening
			peak = true;
		}
		$("div#vendorOrderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 2.5).substring(1)); 
		if (peak == true) {
			$("div#vendorOrderPanel #tollExpense").val(10.25);
		} else {
			$("div#vendorOrderPanel #tollExpense").val(8.25);
		}
	} else if (destinationInfo.County == "Queens County" || destinationInfo.County == "Nassau County" || destinationInfo.County == "Suffolk County" || destinationInfo.County == "Kings County") {
		$("div#vendorOrderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 1.5).substring(1));
	} else if (destinationInfo.County == "New York County") {
		$("div#vendorOrderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 2.5).substring(1));		
	} else if (destinationInfo.County == "Bronx County" || destinationInfo.County == "Westchester County" || destinationInfo.County == "Richmond County") {
		$("div#vendorOrderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 1.5).substring(1));
		$("div#vendorOrderPanel #tollExpense").val(9.60);		
	}
};
