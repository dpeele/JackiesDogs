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

order.currentItem = null; //the current item selected by the user
order.orderItems = []; //the current list of items in this order

order.onload = function () { //called onload of this panel
	 
	order.defaultLabels(); //reset fieldset and panel labels
	
    $(window).resize(function() { //resize main body of form on window resize
        $("div#orderPanel #details").height($(window).height() - ($("div#orderPanel #details").offset().top + 75));
        $("div#orderPanel #leftOrder").height($(window).height() - ($("div#orderPanel #details").offset().top + 65));
        $("div#orderPanel #rightOrder").height($(window).height() - ($("div#orderPanel #details").offset().top + 65));
    });
    $(window).resize();
    
    //customer information display div
    $("div#orderPanel #selectedCustomerDiv").addClass("ui-widget");
    
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#orderPanel :input").addClass("ui-widget").attr("name",getId);
    
	$("div#orderPanel #customerLookup").simpletip({  
		content: "Please enter the first few letters or numbers of the first name, last name, " +
			"email, or phone number of an existing customer or select 'New Customer'",
		fixed: true
	});

	//hide hidden fields
	$("div#orderPanel .hidden").hide();
	
	//hide order item table since it's empty
	$("div#orderPanel #orderItems").hide();

	//set default value of order id to 0
	$("div#orderPanel #orderId").val("0");	
	
    //number only fields
    $("div#orderPanel .onlyNumbers").keydown(order.onlyNumbers).css("width","75px"); //only allow numbers
	
	//fields that require fixed width
	$("div#orderPanel #item").css("width","300px");        
    $("div#orderPanel #city").css("width","100px");
	$("div#orderPanel #email").css("width","200px");	    

	//fields with set max/min lengths
	$("div#orderPanel #phone").css("width","125px").attr("maxlength","14").attr("minlength","10");
    $("div#orderPanel #state").css("width","25px").attr("maxlength","2").attr("minlength","2");
    $("div#orderPanel #zip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    $("div#orderPanel #deliveryZip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    
    //recalculate final cost if any of the inputs are changed
    $("div#orderPanel .cost").change(order.setFinalCost);
    
    //dialogs
	$("div#orderPanel div.dialog").hide(); //set all dialog divs to not be visible
	
	//set value of delivered and personal checkboxes
	$("div#orderPanel :checkbox").attr("value","true");
    
	//buttons- all hidden initially
    $("div#orderPanel #addButton").button().attr("value","Add Item").click(function () {order.checkQuantity(order.addItem,order.currentItem, $("div#orderPanel #quantity").val());}).hide(); //add item to order
    $("div#orderPanel #editCustomerButton").button().attr("value","Edit Customer").click(function () {order.popEditCustomer("Edit Customer");}).hide();//pop customer div
    $("div#orderPanel #submitButton").button().attr("value","Submit Order").click(order.validateAndSubmit).hide();//submit order
    $("div#orderPanel #cancelButton").button().attr("value","Cancel Order").click(order.confirmCancel).hide();//cancel order    
    $("div#orderPanel #enterDeliveryZipButton").button().attr("value","Calculate Delivery").click(order.popDelivery).hide();//pop delivery div
        
    //decimal number only fields
    $("div#orderPanel .onlyDecimalNumbers").keydown(order.onlyNumbersAndDecimalPoint).css("width","75px"); //only allow numbers and a decimal point
            
    
	//quantity field requires fixed width and handler for keyup
	$("div#orderPanel #quantity").css("width","35px").keyup(order.checkForShowAddButton); //when quantity value changes see whether we should show add button
	//set special field widths
	$("div#orderPanel #quantityAvailable").css("width","35px");
    $("div#orderPanel #discount").css("width","25px");
    $("div#orderPanel #changeDue").css("width","50px");
    $("div#orderPanel #credit").css("width","50px");
    $("div#orderPanel #totalWeight").css("width","50px");    
    $("div#orderPanel #deliveryFee").css("width","50px").attr("disabled","true");    
    $("div#orderPanel #tollExpense").css("width","50px").attr("disabled","true");        
    
    //date and time fields
    $("div#orderPanel #deliveryDate").datepicker().css("width","100px").keyup(order.checkForDeliveryButton); //create date picker for optional delivery date
    $("div#orderPanel #deliveryTime").timePicker({startTime: "07:00",endTime: "21:00",show24Hours: false,step: 15}).css("width","75px").keyup(order.checkForDeliveryButton); //create time picker for optional delivery time
   
    
    //display only fields
    $("div#orderPanel .readOnly").attr("readonly",true);//don't allow editing of total price or quantity available

    //set default value for display only total fields
    $("div#orderPanel .total").val("$0.00").css("width","75px");    
    
    //customer autocomplete
    $("div#orderPanel #customerLookup").autocomplete({ //look up client based on last name
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
                    	responseArray.push(order.blankCustomer);
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
				$("div#orderPanel #editCustomerButton").show();//show edit customer button
				order.checkForShowSubmitButton();//see whether we should show submit button
				order.checkForShowCancelButton(); //see whether we should show cancel button
			}
        },        
        minLength: 0
    }).bind("focus",function(){$(this).trigger('keydown.autocomplete');});//force autocomplete to open with just new customer entry on focus 

    //product autocomplete
    $("div#orderPanel #item").autocomplete({ //look up product based on name or id
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
                    		quantity: item.inventory.quantity-item.inventory.reservedQuantity,
                    		totalWeight: item.inventory.totalWeight-item.inventory.reservedWeight,
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
        	order.currentItem = new Item(0, 0, 0, ui.item.productName, ui.item.price, ui.item.billBy, ui.item.estimatedWeight, ui.item.description, ui.item.totalWeight, ui.item.quantity, ui.item.id);
        	$("div#orderPanel #price").val(order.getFormattedPrice());
        	$("div#orderPanel #quantityAvailable").val(order.currentItem.quantityAvailable);
        	$("div#orderPanel #item").val(order.currentItem.name);
        	var estimatedWeight = order.currentItem.estimatedWeight;
        	if (estimated == 1) {
        		estimatedWeight = estimatedWeight + " lb";
        	} else {
        		estimatedWeight = estimatedWeight + " lbs";
        	}
    		$("div#orderPanel #estimatedWeight").val(estimatedWeight);             
    		if (order.currentItem.description.length > 0) { //add tooltip to table row if description exists
    			$(".item").simpletip({  
    				content: order.currentItem.description,
    				fixed: true
    			});
    		}
    		order.checkForShowAddButton();//see whether we should show add button
        },        
        minLength: 2,
    }); 

};

order.defaultLabels = function () {
	$("#orderSpan").html("New Customer Order"); //default order panel label is New Order
	$("div#orderPanel #orderLegend").html("Enter Information:"); //default legend is Enter Information:
	$("panels").tabs("url", "0", "loadOrder"); //dafault url for panel is loadOrder
};

order.checkForShowSubmitButton = function () {
	if (($("div#orderPanel #selectedCustomerDiv").html().length > 0) && ($('#orderItems tr').length > 1)) {
		$("div#orderPanel #submitButton").show();
	}
};

order.checkForShowCancelButton = function () {
	if (($("div#orderPanel #selectedCustomerDiv").html().length > 0) || ($('#orderItems tr').length > 1)) {
		$("div#orderPanel #cancelButton").show();
	}
};

order.checkForShowAddButton = function () {
	if ($("div#orderPanel #item").val().length > 0) {
		$("div#orderPanel #addButton").show();
	}
};

order.setFinalCost = function () {
	var totalCost = $("div#orderPanel #totalCost").val();
	var finalCost = totalCost - ((totalCost / 100) * $("div#orderPanel #discount").val());  //subtract food discount from order total
	finalCost = finalCost - $("div#orderPanel #credit").val() + $("div#orderPanel #deliveryFee").val() + $("div#orderPanel #tollExpense").val(); //add other costs and subtract credit from order total	
	$("div#orderPanel #finalCost").val(finalCost);
};

order.setFloatValue = function (value, name) { //if the float value isn't zero, set field name to formatted value
	if (parseFloat(value != 0)) {
		$("div#orderPanel #"+name).val(formatPrice(value));
	}
};

order.setValues = function (id,deliveryDate,deliveryTime,discount,credit,deliveryFee,tollExpense,totalCost,totalWeight,status,changeDue,delivered,personal) {
	var totalFoodCost = totalCost + ((totalCost / 100) * discount);  //add food discount to order total
	totalFoodCost = totalCost + credit - deliveryFee - tollExpense; //subtract other costs and add credit to order total to give us the total of just the food
	$("div#orderPanel #totalCost").val($+formatPrice(totalFoodCost)); //set total food cost
	$("div#orderPanel #totalWeight").val(totalWeight); //set total weight	
	$("div#orderPanel #finalCost").val(formatPrice(totalCost)); //set total food cost		
	if (parseInt(credit) != 0) { //if there is a credit
		$("div#orderPanel #credit").val(credit);
	}
	order.setFloatValue(discount,"discount"); //set the double values
	order.setFloatValue(deliveryFee,"deliveryFee");
	order.setFloatValue(tollExpense,"tollExpense");
	order.setFloatValue(changeDue,"changeDue");
	$("div#orderPanel select option[value='"+status+"']").attr("selected","selected"); //select the correct status option
	$("div#orderPanel #orderId").val(id); //set order id
	$("div#orderPanel #deliveryDate").val(deliveryDate); //set delivery date and time
	$("div#orderPanel #deliveryTime").val(deliveryTime);
	if (delivered == "true") { //if delivered is true check delivered checkbox
		$("div#orderPanel #delivered").attr("value","true");
	}
	if (personal == "true") { //if personal is true check personal checkbox
		$("div#orderPanel #personal").attr("value","true");
	}	
	
	//show buttons where appropriate
	order.checkForShowSubmitButton();
	order.checkForShowCancelButton();		
	order.checkForDeliveryButton();
	$("div#orderPanel #editCustomerButton").show();
	$("#orderSpan").html("Edit Customer Order"); //change order panel label to Edit Order 
	$("div#orderPanel #orderLegend").html("Edit Information:"); //change legend to Edit Information:	
};

order.checkForDeliveryButton = function () {
	if (($("div#orderPanel #deliveryDate").val().length > 0) && ($("div#orderPanel deliveryTime").val().length > 0)) {
		$("div#orderPanel #enterDeliveryZipButton").show();
        $("div#orderPanel #deliveryFee").attr("disabled","false");    
        $("div#orderPanel #tollExpense").attr("disabled","false");  
	}	
};

order.checkQuantity = function (destinationFunction, item, quantity) { //check quantity ordered vs quantity in stock and confirm if quantity ordered is greater
	var quantityCheck =item.availableQuantity - quantity; //make sure you aren't adding more than we have
	if (quantityCheck < 0) {
		$("div#orderPanel #quantityOverrideDialog").dialog({ 
			modal: true, 
			buttons: [ 
				{ text: "Continue", click:function() { 
					$(this).dialog("close");
					destinationFunction(item, quantity);									
				}}, 	
				{ text: "Cancel", click:function() {        					 
					$(this).dialog( "close" ); 
		}}]});		
	} else {
		destinationFunction(item, quantity);
	}
};

//update the total quantity and weight
order.updateTotalQuantity = function (item, newQuantity) {
	//get current total weight
	var totalWeight = parseFloat($("div#orderPanel #totalWeight").val());
	//get old weight				
	var oldWeight = item.weight;
	//get old cost for item
	var oldCost = item.getUnformattedTotalPrice();
	// set quantity of this item to new quantity
	item.quantity = newQuantity;
	// set weight of this item to new weight
	item.weight = parseFloat(item.quantity * item.estimatedWeight);
	//set weight in item table
	$("div#orderPanel #totalWeight").val(totalWeight - oldWeight + item.weight);
	//get previous total food cost
	var totalCost = parseFloat($("div#orderPanel #totalCost").val());
	//get new cost for item
	var newCost = item.getUnformattedTotalPrice();
	$("div#orderPanel #totalCost").val("$"+formatPrice(totalCost-oldCost+newCost));//update total
	$("div#orderPanel #totalItemPrice"+item.productId).html(item.getFormattedTotalPrice());
};

//update total quantity, total weight, item quantity, and item weight
order.updateTotalQuantityAndWeight = function (item, newWeight) {
	item.estimated = false;
	// remove weight of this item from total weight and add in new weight
	var totalWeight = parseFloat($("div#orderPanel #totalWeight").val());
	//get old cost for item
	var oldCost = item.getUnformattedTotalPrice();
	// set weight of this item to new weight
	item.weight = newWeight;
	//set weight in item table
	$("div#orderPanel #totalWeight").val(totalWeight - oldWeight + item.weight);
	// set quantity of this item to new quantity
	item.quantity = $("div#orderPanel #exactQuantity").val();						
	//set quantity in item table
	$("div#orderPanel #quantity"+item.productId).val(item.getFormattedQuantityAndWeight());
	//get previous total food cost
	var totalCost = parseFloat($("div#orderPanel #totalCost").val());
	//get new cost for item
	var newCost = item.getUnformattedTotalPrice();
	$("div#orderPanel #totalCost").val("$"+formatPrice(totalCost-oldCost+newCost));//update total
	$("div#orderPanel #totalItemPrice"+item.productId).html(item.getFormattedTotalPrice());
};

order.addItem = function (item, quantity) { //add item to order
	if ($("div#orderPanel #orderItems tr").length == 1) {
		$("div#orderPanel #orderItems").show();
	}
	if (quantity != null) {//new item passed from form, not existing item passed from loaded order	
		if (order.orderItems[item.productId] != null) { //this item is already in the table
			$("div#orderPanel #itemAlreadyAddedDialog").dialog({ modal: true });
			return;				
		}
		item.quantity = quantity;		
		item.weight = parseFloat(item.quantity * item.estimatedWeight);	
		order.orderItems[item.productId] = item;
		order.currentItem = {};
	}
	$("div#orderPanel #orderItems tr:last").after("<tr>\n" + //add row
										"<td id='productId"+item.productId+"'>"+item.productId+"</td>\n" +
										"<td>"+item.name+"</td>\n" +
										"<td><input type='text' id='quantity"+item.productId+"' value='"+item.getFormattedQuantityAndWeight()+"'/></td>\n" +
										"<td>"+item.getFormattedPrice()+"</td>\n" +
										"<td id='totalItemPrice"+item.productId+"'>"+item.getFormattedTotalPrice() + "</td>\n" +
										"<td><input type='button' id='button"+item.productId+"'/></td>\n" +
									"</tr>\n");
		
	$("div#orderPanel #quantity"+item.productId).css("width","100px"); //set width of quantity input		
	if (item.description.length > 0) { //add tooltip to table row if description exists
		$("div#orderPanel #orderItems tr:last").simpletip({  
			content: description,
			fixed: true
		});
	}	
		
	if (item.isByThePound()) { //priced by the pound, add focus event handler to pop up estimated dialog and handle that
		$("div#orderPanel #quantity"+item.productId).focus(function() {
			$("div#orderPanel #exactQuantity").val(item.quantity);//set initial quantity value in dialog to value from table row
			var oldWeight = item.weight; //get old weight
			$("div#orderPanel #exactWeight").val(oldWeight);//set initial weight value in dialog to value from table row
			var dialog = $("div#orderPanel #editPoundQuantityDialog").dialog({ 
				autoOpen: false,
				modal: true, 
				title: "Enter Exact Weight",
				width: 400,
				buttons: [ 
					{ text: "Submit", click:function() { 
						if (!($("div#orderPanel #exactQuantityForm").validate().form()) || $("div#orderPanel #exactWeight").val()==".") {//validate form
							$("div#orderPanel #incompletePoundQuantityDialog").dialog({ modal: true });
							return;	
						}		
						var newWeight = parseFloat($("div#orderPanel #exactWeight").val());
						if (newWeight > item.totalWeight) {
							$("div#orderPanel #quantityOverrideDialog").dialog({ 
								modal: true, 
								buttons: [ 
									{ text: "Continue", click:function() { 
										$(this).dialog("close");
										order.UpdateTotalQuantityAndWeight(item, newWeight);		
										$(this).dialog("close");										
									}}, 	
									{ text: "Cancel", click:function() {        					 
										$(this).dialog( "close" ); 
							}}]});		
						} else {
							order.UpdateTotalQuantityAndWeight(item, newWeight);
							$(this).dialog("close");							
						}
					}}, 	
						{ 
							text: "Cancel", 
							click:function() {        					 
								$(this).dialog("close"); 
			}}]});
			// Take whole dialog and put it back into the custom scope
			dialog.parent(".ui-dialog").appendTo("div#orderPanel");
			// Open the dialog (if you want autoOpen)
			dialog.dialog("open");				
		});	
	} else { //not sold by the pound, set width and add keydown handler and adjust price as they type
		$("div#orderPanel #quantity"+item.productId).keydown(function(event){ //add keydown event handler to quantity text box to allow 
			//only numbers and a decimal point and update price on quantity change
			if(order.onlyNumbers(event)) {//value may have changed, update total price
				//get new quantity
				var newQuantity = $("div#orderPanel #quantity"+item.productId).val();
				if (newQuantity > item.totalQuantity) {
					order.checkQuantity(order.updateTotalQuantity,newQuantity);
				} else {
					order.updateTotalQuantity(newQuantity);
				}
			}
		});	
	}
				            			
	$("div#orderPanel #button"+item.productId).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		$(this).closest('tr').hide();  //remove row
		$("div#orderPanel #removed"+rowValue).val("true");
		if ($("div#orderPanel #orderItems tr").length == 1) { //hide table if it's empty
			$("div#orderPanel #orderItems").hide();
		}	
		//get weight for item
		var weight = item.weight;
		//get cost for item
		var cost = item.getUnformattedTotalPrice();
		//get total weight 
		var totalWeight = parseFloat($("div#orderPanel #totalWeight").val());
		//get total cost
		var totalCost = parseFloat($("div#orderPanel #totalCost").val());
		//remove cost of this item from total food cost
		$("div#orderPanel #totalCost").val("$"+formatPrice(totalCost - cost));//update total
		// remove weight of this item from total weight
		$("div#orderPanel #totalWeight").val(totalWeight-weight);
		item.remove();//set remove flag of item in array
	});//remove item from order
	$("div#orderPanel .item").val(""); //reset item fields
	$("div#orderPanel #addButton").hide();
	order.checkForShowSubmitButton(); //see whether we should show submit button
	order.checkForShowCancelButton(); //see whether we should show cancel button
	
	if ($("div#orderPanel #totalCost").val() != "") { // update total price and weight
		//get total cost
		var totalCost = parseFloat($("div#orderPanel #totalCost").val());
		//get total weight 
		var totalWeight = parseFloat($("div#orderPanel #totalWeight").val());
		//get cost for item
		var cost = item.getUnformattedTotalPrice();
		//get weight for item
		$("div#orderPanel #totalCost").val("$"+formatPrice(totalCost+cost));
		$("div#orderPanel #totalWeight").val(totalWeight+item.weight);
	} else { //set price and weight
		$("div#orderPanel #totalCost").val(item.getformattedTotalPrice());
		$("div#orderPanel #totalWeight").val(item.weight);
	}	
};

order.populateCustomer = function(item) { //populate customer dialog fields from json object
	$("div#orderPanel #firstName").val(item.firstName);
	$("div#orderPanel #lastName").val(item.lastName);
    $("div#orderPanel #streetAddress").val(item.streetAddress);
 	$("div#orderPanel #aptAddress").val(item.aptAddress);    
    $("div#orderPanel #phone").val(item.phone);
    $("div#orderPanel #state").val(item.state);
    $("div#orderPanel #city").val(item.city);
    $("div#orderPanel #zip").val(item.zip);
    $("div#orderPanel #email").val(item.email);
    $("div#orderPanel #custId").val(item.custId);  
    $("div#orderPanel #editCustId").val(item.custId);        		   
};

order.addInfo = function (customerInfo, currentItem, index) {
	var value = $(currentItem).val();
	if (value.trim().length != 0) {
		if (index==2) {
			customerInfo = customerInfo + value + ", ";
		} else if (index==3) {
			customerInfo = customerInfo + value + " ";
		} else {		
			customerInfo = customerInfo + value + "<br/>";
		}
	}
	return (customerInfo);
};

order.populateCustomerDiv = function() { //populate customer div from customer dialog fields
	var customerInfo = $("div#orderPanel #firstName").val() + " " + $("div#orderPanel #lastName").val() + "<br/>";
	var additionalInfo = "";
	if ($("div#orderPanel #phone").val().length != 0) {
   		additionalInfo = additionalInfo + "phone: " + $("div#orderPanel #phone").val() + ", ";
	}
	if ($("div#orderPanel #email").val().length != 0) {
   		additionalInfo = additionalInfo + "email: " + $("div#orderPanel #email").val() + "<br/>";
	} else {
		additionalInfo = additionalInfo.substring(0,additionalInfo.length-2) + "<br/>"; //remove ", " and add line break if there isn't an email address 
	}
	if ($.makeArray($("div#orderPanel .customerAddress")) > 0) {
		if ($.makeArray($("div#orderPanel .customerAddress")) == 1) {
			customerInfo = order.addInfo (customerInfo, $.makeArray($("div#orderPanel .customerAddress"))[0]);
		} else {
			customerInfo = $.makeArray($("div#orderPanel .customerAddress")).reduce(order.addInfo, customerInfo); //reduce address fields to single piece of HTML
		}
	}
	$("div#orderPanel #selectedCustomerDiv").html(customerInfo+additionalInfo); //populate customer div with customer information
};

order.popDelivery = function () { //pop delivery calculator div
	$("div#orderPanel #deliveryZip").val($("div#orderPanel #zip").val()); //prepopulate with customer's zip
	$("div#orderPanel #peak").attr("checked",false);
	var dialog = $("div#orderPanel #enterDeliveryZipDialog").dialog({ 
		autoOpen: false,
		modal: true, 
		title: "Enter Delivery Zip to Calculate Fee and Tolls",
		width: 400,
		buttons: [ 
			{ text: "Submit", click:function() { 
				if (!($("div#orderPanel #enterDeliveryZipForm").validate().form())) {//validate form
					$("div#orderPanel #incompleteDeliveryDialog").dialog({ modal: true });
					return;	
				}			
				order.requestDestinationData($("div#orderPanel #deliveryZip").val());
				$(this).dialog("close"); 
			}}, 	
				{ 
					text: "Cancel", 
					click:function() {        					 
						$(this).dialog("close"); 
	}}]});	
	// Take whole dialog and put it back into the custom scope
	dialog.parent(".ui-dialog").appendTo("div#orderPanel");
	// Open the dialog (if you want autoOpen)
	dialog.dialog("open");	
};

order.popEditCustomer = function (dialogTitle) { //pop up customer edit dialog box
	var dialog = $("div#orderPanel #customerDialog").dialog({ 
		autoOpen: false,
		modal: true,
		title: dialogTitle,
		width: 500,
		buttons: [ { 
			text: "Save", 
			click: function() { 
				$("div#orderPanel #confirmCustomerSaveDialog").dialog({ 
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
    			$("div#orderPanel #confirmCustomerCancelDialog").dialog({ 
        			modal: true, 
        			buttons: [ 
        				{ 
        					text: "Yes", 
        					click: function() { 
        						$(this).dialog("close");
								$("div#orderPanel #customerDialog").dialog("close");
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
	    		$("div#orderPanel #confirmClearDialog").dialog(
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
	// Take whole dialog and put it back into the custom scope
	dialog.parent(".ui-dialog").appendTo("div#orderPanel");
	// Open the dialog (if you want autoOpen)
	dialog.dialog("open");
};

order.clearCustomer = function () { //clear customer data
	$("div#orderPanel #customerForm input[type=text]").val("");
};

order.validateAndSubmitCustomer = function () { //validate and submit customer dialog via ajax
	if ((!($("div#orderPanel #customerForm").validate().form())) || (($("div#orderPanel #email").val().length == 0) && ($("div#orderPanel #phone").val().length == 0))) {//validate form
		$("div#orderPanel #incompleteCustomerDialog").dialog({ modal: true });
		return;	
	}
	//console.log($("div#orderPanel #customerForm").serialize());
	console.log($("#customerForm").serialize());
    $.ajax({ //make ajax call to submit order
        url: "customerUpdate",
        cache: false,
        type: "post",  
        dataType: "json",      
        data: $("div#orderPanel #customerForm").serialize(),
        success: function( data ) { //order submission succeeded
        	order.populateCustomerDiv();
        	$("div#orderPanel #custId").val(data.custId); //populate customer id on order form with correct customer id
        	$("div#orderPanel #editCustomerButton").show();//show edit customer button
        	order.checkForShowSubmitButton();//see whether we should show submit button
        	order.checkForShowCancelButton(); //see whether we should show cancel button
        },
        error: function () { //order submission failed
        	$("div#orderPanel #customerSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });	
	$("div#orderPanel #customerDialog").dialog("close");
};

order.validateAndSubmit = function () {
	
	var orderItemQueryString = "";
	if (order.orderItems.length > 0) {
		if (order.orderItems.length == 1) {
			orderItemQueryString = escape(ExtractItemData("", order.orderItems[0]));
		} else {
			orderItemQueryString = escape(order.orderItems.reduce(ExtractItemData));
		}
	}
	
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: orderItemQueryString + $("div#orderPanel #orderForm").serialize(),
        success: function( data ) { //order submission succeeded
        	if (data.estimates) {
        		$("div#orderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been submitted. Your total is $" + formatPrice(totalCost)+". You have estimated items in your order so this is a preliminary total and you will be contact shortly with your final bill.").dialog({ modal: true }); //pop success dialog
        	} else {
        		$("div#orderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been submitted. Total cost is $" + formatPrice(totalCost)+".").dialog({ modal: true }); //pop success dialog
        	}
        	order.updateOrderItemIds(data.newOrderItems);
        	$("div#orderPanel #orderId").val(data.orderId); //set order id
        },
        error: function () { //order submission failed
        	$("div#orderPanel #orderSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

//set the id of any orderItems that were new on this submit 
order.updateOrderItemIds = function (newOrderItems) {
	for (orderItem in newOrderItems) {
		order.orderItems[orderItem.product.id].id = orderItem.id;
	}
};

order.resetForm = function () {
	$("div#orderPanel #orderItems tr:gt(0)").remove(); //empty order table except for header
	$("div#orderPanel input[type=text]").val(""); //empty all fields    	
	$("div#orderPanel #selectedCustomerDiv").html("");
	//hide buttons
    $("div#orderPanel #addButton").hide();
    $("div#orderPanel #editCustomerButton").hide();
    $("div#orderPanel #submitButton").hide();
    $("div#orderPanel #enterDeliveryZipButton").hide();
    //disable delivery fields
    $("div#orderPanel #deliveryFee").attr("disabled","true");    
    $("div#orderPanel #tollExpense").attr("disabled","true");    
    $("div#orderPanel #orderId").val("0");   //set orderId to blank order
    order.defaultLabels(); //reset fieldset and panel labels
    $("div#orderPanel #orderItems").hide(); //hide order item table since it's now empty
    order.orderItems = [];
};

order.confirmCancel = function () { //confirm order cancelation
		$("div#orderPanel #confirmCancelDialog").dialog({ 
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
	if ($("div#orderPanel #orderId").val() == "0") {
		order.resetForm();
		return;
	}
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("div#orderPanel #orderForm").serialize()+"&cancelled=" + encodeURIComponent("Cancelled"),//append cancelled parameter to form data
        success: function( data ) { //order submission succeeded
        	$("div#orderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been cancelled.").dialog({ modal: true }); //pop successful cancellation dialog
    		order.resetForm();    
        },
        error: function () { //order submission failed
        	$("div#orderPanel #orderCancellationFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

order.addOrderItems = function () { //add all items of existing order
	for (item in order.orderItems) {
		order.addItem(item);
	}
};

order.extractItemData = function (string, currentValue) {//retrieve data from order array element and add it to string
	if (currentValue.isRemoved() && currentValue.id == 0) {
		return string;
	} else {
		return (string+"items=id="+currentValue.productId+"#quantity="+currentValue.quantity+
				+"#dbId="+currentValue.id+"#removed="+currentValue.isRemoved()+"#estimate="+currentValue.isEstimate()+"#weight="+currentValue.weight+"&");
	}
};

order.onlyNumbersAndDecimalPoint = function (event) { //only allow numbers to be entered in this text input
    if ( event.keyCode == 190 ) { //if the user pressed a decimal point
    	if ($("div#orderPanel #"+event.target.id).val().indexOf(".") == -1) { //if we don't already have a decimal point in the number then allow it
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
	   var seconds2 = hour2 * 3600 + minute2 * 60; //convert time2 to seconds
	   return (seconds1 - seconds2); // Gets difference in seconds
};

order.populateDeliveryInfo = function (destinationInfo) {
	if (destinationInfo.state == "NJ") { //determine delivery fee and toll based on distance, county, and state
		//determine if this a peak time as tolls are different for peak and off-peak
		var dateArray = $("div#orderPanel #deliveryDate").val().split("/"); //split date from deliveryDate field
		var dayOfWeek = new Date(dateArray[2], dateArray[0]-1, dateArray[1]).getDay();
		var deliveryTime = parseInt($("div#orderPanel #deliveryTime").val()); //get time from deliveryTime field
		var peak = false;
		if (dayOfWeek == 0 || dayOfWeek == 6) { //this is a weekend date 
			if ((order.compareTime(deliveryTime,"10:00 AM")>0) && (order.compareTime("9:00 PM",deliveryTime)>0)) { // if delivery time is after 10AM in the morning and before 9PM in the evening
				peak = true;
			}
		} else /*weekday*/ if (((order.compareTime(deliveryTime,"5:00 AM")>0) && (order.compareTime("10:00 AM",deliveryTime)>0)) || // if delivery time is between 5AM and 10AM in the morning 
					(order.compareTime(deliveryTime,"3:00 PM")>0) && (order.compareTime("8:00 PM",deliveryTime)>0)) { // or if delivery time is between 3PM and 8PM in the evening
			peak = true;
		}
		$("div#orderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 2.5)); 
		if (peak == true) {
			$("div#orderPanel #tollExpense").val(10.25);
		} else {
			$("div#orderPanel #tollExpense").val(8.25);
		}
	} else if (destinationInfo.County == "Queens County" || destinationInfo.County == "Nassau County" || destinationInfo.County == "Suffolk County" || destinationInfo.County == "Kings County") {
		$("div#orderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 1.5));
	} else if (destinationInfo.County == "New York County") {
		$("div#orderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 2.5));		
	} else if (destinationInfo.County == "Bronx County" || destinationInfo.County == "Westchester County" || destinationInfo.County == "Richmond County") {
		$("div#orderPanel #deliveryFee").val(formatPrice(destinationInfo.distance * 1.5));
		$("div#orderPanel #tollExpense").val(9.60);		
	}
};
