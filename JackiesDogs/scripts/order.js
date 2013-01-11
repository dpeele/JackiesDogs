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
        $("#details").height($(window).height() - ($("#details").offset().top + 75));
        $("#leftOrder").height($(window).height() - ($("#details").offset().top + 65));
        $("#rightOrder").height($(window).height() - ($("#details").offset().top + 65));
    });
    $(window).resize();
    
    //customer information display div
    $("#selectedCustomerDiv").addClass("ui-widget");
    
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $(":input").addClass("ui-widget").attr("name",$(this).attr("id"));
    
	$("#customerLookup").simpletip({  
		content: "Please enter the first few letters or numbers of the first name, last name, " +
			"email, or phone number of an existing customer or select 'New Customer'",
		fixed: true
	});

	//hide hidden fields
	$(".hidden").hide();
	
	//hide order item table since it's empty
	$("#orderItems").hide();

	//set default value of order id to 0
	$("#orderId").val("0");	

	//quantity field requires fixed width and handler for keyup
	$("#quantity").css("width","75px").keyup(order.checkForShowAddButton); //when quantity value changes see whether we should show add button
	
	//fields that require fixed width
	$("#quantityAvailable").css("width","75px");
	$("#item").css("width","300px");        
    $("#city").css("width","100px");
	$("#email").css("width","200px");	    

	//fields with set max/min lengths
	$("#phone").css("width","125px").attr("maxlength","14").attr("minlength","10");
    $("#state").css("width","25px").attr("maxlength","2").attr("minlength","2");
    $("#zip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    $("#deliveryZip").css("width","50px").attr("maxlength","5").attr("minlength","5");
    
    //recalculate final cost if any of the inputs are changed
    $(".cost").change(order.setFinalCost);
    
    //dialogs
	$("div.dialog").hide(); //set all dialog divs to not be visible
	
	//set value of delivered and personal checkboxes
	$(":checkbox").attr("value","true");
    
	//buttons- all hidden initially
    $("#addButton").button().attr("value","Add Item").click(order.addItem).hide(); //add item to order
    $("#editCustomerButton").button().attr("value","Edit Customer").click(function () {order.popEditCustomer("Edit Customer");}).hide();//pop customer div
    $("#submitButton").button().attr("value","Submit Order").click(order.validateAndSubmit).hide();//submit order
    $("#cancelButton").button().attr("value","Cancel Order").click(order.confirmCancel).hide();//cancel order    
    $("#enterDeliveryZipButton").button().attr("value","Calculate Delivery").click(order.popDelivery).hide();//pop delivery div
    
    //number only fields
    $(".onlyNumbers").keydown(order.onlyNumbers).css("width","75px"); //only allow numbers
    
    //set discount field widths
    $("#discount").css("width","25px");
    $("#changeDue").css("width","50px");
    $("#discount").css("width","25px");
    
    //decimal number only fields
    $(".onlyDecimalNumbers").keydown(order.onlyNumbersAndDecimalPoint).css("width","75px"); //only allow numbers and a decimal point
            
    //set field widths
    $("#quantity").css("width","35px");
    $("#quantityAvailable").css("width","35px");
    $("#discount").css("width","25px");
    $("#changeDue").css("width","50px");
    $("#credit").css("width","50px");
    $("#deliveryFee").css("width","50px").attr("disabled","true");    
    $("#tollExpense").css("width","50px").attr("disabled","true");        
    
    //date and time fields
    $("#deliveryDate").datepicker().css("width","100px").keyup(order.checkForDeliveryButton); //create date picker for optional delivery date
    $("#deliveryTime").timePicker({startTime: "07:00",endTime: "21:00",show24Hours: false,step: 15}).css("width","75px").keyup(order.checkForDeliveryButton); //create time picker for optional delivery time
   
    
    //display only fields
    $(".readOnly").attr("readonly",true);//don't allow editing of total price or quantity available

    //set default value for display only total fields
    $(".total").val("$0.00").css("width","75px");    
    
    //customer autocomplete
    $("#customerLookup").autocomplete({ //look up client based on last name
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
				$("#editCustomerButton").show();//show edit customer button
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
        	$("#itemId").val(ui.item.id);
        	$("#price").val(ui.item.price);
        	$("#quantityAvailable").val(ui.item.quantity);
        	$("#item").val(ui.item.productName);
    		$("#billBy").val(ui.item.billBy); 
    		$("#estimatedWeight").val(ui.item.estimatedWeight);             
    		$("#description").val(ui.item.description);
    		order.checkForShowAddButton();//see whether we should show add button
        },        
        minLength: 2,
    }); 

};

order.defaultLabels = function () {
	$("#orderAnchor").html("New Customer Order"); //default order panel label is New Order
	$("#orderLegend").html("Enter Information:"); //default legend is Enter Information:
	 $("#orderAnchor").attr("href","loadOrder"); //dafault url for panel is loadOrder
};

order.checkForShowSubmitButton = function () {
	if (($("#selectedCustomerDiv").html().length > 0) && ($('#orderItems tr').length > 1)) {
		$("#submitButton").show();
	}
};

order.checkForShowCancelButton = function () {
	if (($("#selectedCustomerDiv").html().length > 0) || ($('#orderItems tr').length > 1)) {
		$("#cancelButton").show();
	}
};

order.checkForShowAddButton = function () {
	if (($("#itemId").val().length > 0) && ($("#quantity").val().length > 0)) {
		$("#addButton").show();
	}
};

order.setFinalCost = function () {
	var totalCost = $("#totalCost").val();
	var finalCost = totalCost - ((totalCost / 100) * $("#discount").val());  //subtract food discount from order total
	finalCost = finalCost - $("#credit").val() + $("#deliveryFee").val() + $("#tollExpense").val(); //add other costs and subtract credit from order total	
	$("#finalCost").val(finalCost);
}

order.setFloatValue = function (value, name) { //if the float value isn't zero, set field name to formatted value
	if (parseFloat(value != 0)) {
		$("#"+name).val(formatPrice(value).substring(1));
	}
}

order.setValues = function (id,deliveryDate,deliveryTime,discount,credit,deliveryFee,tollExpense,totalCost,status,changeDue,delivered,personal) {
	var totalFoodCost = totalCost + ((totalCost / 100) * discount);  //add food discount to order total
	totalFoodCost = totalCost + credit - deliveryFee - tollExpense; //subtract other costs and add credit to order total to give us the total of just the food
	$("#totalCost").val(totalFoodCost); //set total food cost	
	if (parseInt(credit) != 0) { //if there is a credit
		$("#credit").val(credit);
	}
	order.setFloatValue(discount,"discount"); //set the double values
	order.setFloatValue(deliveryFee,"deliveryFee");
	order.setFloatValue(tollExpense,"tollExpense");
	order.setFloatValue(changeDue,"changeDue");
	$("select option[value='"+status+"']").attr("selected","selected"); //select the correct status option
	$("#orderId").val(id); //set order id
	$("#deliveryDate").val(deliveryDate); //set delivery date and time
	$("#deliveryTime").val(deliveryTime);
	if (delivered == "true") { //if delivered is true check delivered checkbox
		$("#delivered").attr("value","true");
	}
	if (personal == "true") { //if personal is true check personal checkbox
		$("#personal").attr("value","true");
	}	
	
	//show buttons where appropriate
	order.checkForShowSubmitButton();
	order.checkForShowCancelButton();		
	order.checkForDeliveryButton();
	$("#editCustomerButton").show();
	$("#orderAnchor").html("Edit Customer Order"); //change order panel label to Edit Order 
	$("#orderLegend").html("Edit Information:"); //change legend to Edit Information:	
};

order.checkForDeliveryButton = function () {
	if (($("#deliveryDate").val().length > 0) && ($("deliveryTime").val().length > 0)) {
		$("#enterDeliveryZipButton").show();
        $("#deliveryFee").attr("disabled","false");    
        $("#tollExpense").attr("disabled","false");  
	}	
};

order.checkQuantity = function () { //check quantity ordered vs quantity in stock and confirm if quantity ordered is greater
	var quantityCheck =$("#quantityAvailable").val() -$("#quantity").val(); //make sure you aren't adding more than we have
	if (quantityCheck < 0) {
		$("#quantityOverrideDialog").dialog({ 
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
	if ($("#orderItems tr").length == 1) {
		$("#orderItems").show();
	}
	var totalItemPrice;
	var estimate;
	var byThePound;
	if (arguments.length == 0) {//not an existing item
		id=0;
		billBy=$("#billBy").val();
		quantity=$("#quantity").val();
		price=$("#price").val();
		estimatedWeight=$("#estimatedWeight").val();
		itemId=$("#itemId").val();
		name=$("#item").val();
		description=$("#description").val();
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
	$("#orderItems tr:last").after("<tr>\n" + //add row
										"<td>"+itemId+"</td>\n" +
										"<td id='dbIdTd"+rowValue+"'><input type='text'/></td>\n" +
										"<td>"+name+"</td>\n" +
										"<td><input type='text' id='quantity"+rowValue+"'/></td>\n" +
										"<td>" + formatPrice(price)+"</td>\n" +
										"<td id='totalItemPrice"+rowValue+"'>" + formattedtotalCost + "</td>\n" +
										"<td><input type='button' id='button" +	rowValue+"'/></td>\n" +
										"<td id='removedTd"+rowValue+"'><input type='text' id='removed"+rowValue+"'/></td>\n" +
									"</tr>\n");
		
	$("#removedTd"+rowValue).hide(); //hide td holding field to indicate whether or not row has been hidden
	$("#")
	$("#dbIdTd"+rowValue).hide().find(":text").val(id); //hide td holding field and set dbId inside
	$("#totalItemPriceUnformatted"+rowValue).css("display","none"); //hide field to store unformatted total price
		
	if (description.length > 0) { //add tooltip to table row if description exists
		$("#orderItems tr:last").simpletip({  
			content: description,
			fixed: true
		});
	}	
		
	if (byThePound ) { //priced by the pound, add focus event handler to pop up estimated dialog and handle that
		if (estimate) {
			$("#quantity"+rowValue).val(quantity+" (" + quantity*estimatedWeight + "lbs)");//estimate weight from quantity
		} else {
			$("#quantity"+rowValue).val(quantity+" (" + weight + "lbs)");//use exact weight
		}
		$("#quantity"+rowValue).focus(function() {
			var exactQuantity = $("#quantity"+rowValue).val();
			var exactWeight = "";
			var start = quantity.indexOf(" ("); //location of front parenthesis if it exists
			if (start != -1) { //front parenthesis exists and we have an exact weight for this item
				exactWeight = exactQuantity.substring(start+2, exactQuantity.indexOf("lbs)")); //strip out weight
				exactQuantity = exactQuantity.substring(0,start); //strip out quantity	
			}
			$("#exactQuantity").val(exactQuantity);//set initial quantity value in dialog to value from table row
			$("#exactWeight").val(exactWeight);//set initial weight value in dialog to value from table row
			$("#editPoundQuantityDialog").dialog({ 
				modal: true, 
				title: "Enter Exact Weight",
				width: 400,
				buttons: [ 
					{ text: "Submit", click:function() { 
						if (!($("#exactQuantityForm").validate().form()) || $("#exactWeight").val()==".") {//validate form
							$("#incompletePoundQuantityDialog").dialog({ modal: true });
							return;	
						}																			
						$("#quantity"+rowValue).val($("#exactQuantity").val()+" (" + $("#exactWeight").val() + "lbs)");
						var exacttotalCost= formatPrice(parseFloat($("#exactWeight").val()) * $("#price").val());
						var exacttotalCostNumeric = exacttotalCost.substring(1);
						var totalItemPriceNumeric;
						var index = $("#totalItemPrice"+rowValue).html().indexOf(" (est)");
						if (index == -1) {
							totalItemPriceNumeric = $("#totalItemPrice"+rowValue).html().substring(1);
						} else {
							totalItemPriceNumeric = $("#totalItemPrice"+rowValue).html().substring(1,index);
						}
						$("#totalCost").val(formatPrice(parseFloat($("#totalCost").val().substring(1))-totalItemPriceNumeric+exacttotalCostNumeric));//update total
						$("#totalItemPrice"+rowValue).html(exacttotalCost);
						$("#totalItemPriceUnformatted"+rowValue).val(exacttotalCostNumeric);
						$(this).dialog("close");
					}}, 	
						{ 
							text: "Cancel", 
							click:function() {        					 
								$(this).dialog("close"); 
			}}]});
		});	
	} else { //not estimate, add keydown handler and adjust price as they type
		$("#quantity"+rowValue).val(quantity).css("width","100px"); //set quantity
		$("#quantity"+rowValue).keydown(function(event){ //add keydown event handler to quantity text box to allow 
														 //only numbers and a decimal point and update price on quantity change			
			if(order.onlyNumbers(event)) {//value may have changed, update total price
				var totalItemPriceNumeric = $("#totalItemPrice"+rowValue).html().substring(1);			
				if ($("#quantity"+rowValue).val().length == 0) { //no quantity
					$("#totalCost").val(formatPrice(parseFloat($("#totalCost").val().substring(1))-totalItemPriceNumeric));//update total
					$("#totalItemPrice"+rowValue).html("$0.00");
				} else {//handle new >0 quantity
					var exacttotalCost= formatPrice(parseFloat($("#quantity"+rowValue).val()) * $("#price").val());
					var exacttotalCostNumeric = exacttotalCost.substring(1);						
					$("#totalCost").val(formatPrice(parseFloat($("#totalCost").val().substring(1))-totalItemPriceNumeric+exacttotalCostNumeric));//update total
					$("#totalItemPrice"+rowValue).html(exacttotalCost);					
				}
			}
		});	
	}
				            			
	$("#button"+rowValue).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		var totalItemPriceNumeric;
		var index = $("#totalItemPrice"+rowValue).html().indexOf(" (est)");
		if (index == -1) {
			totalItemPriceNumeric = $("#totalItemPrice"+rowValue).html().substring(1);
		} else {
			totalItemPriceNumeric = $("#totalItemPrice"+rowValue).html().substring(1,index);
		}
		$("#totalCost").val(formatPrice(parseFloat($("#totalCost").val().substring(1))-totalItemPriceNumeric));//update total
		$(this).closest('tr').hide();  //remove row
		$("#removed"+rowValue).val("true");
		if ($("#orderItems tr").length == 1) {
			$("#orderItems").hide();
		}		
	});//remove item from order

	$(".item").val(""); //reset item fields
	$("#addButton").hide();
	order.checkForShowSubmitButton(); //see whether we should show submit button
	order.checkForShowCancelButton(); //see whether we should show cancel button
	if ($("#totalCost").val() != "") { // update total price
		$("#totalCost").val(formatPrice(parseFloat($("#totalCost").val().substring(1))+totalItemPrice));
	} else {
		$("#totalCost").val(formatPrice(totalItemPrice));
	}
};

order.populateCustomer = function(item) { //populate customer dialog fields from json object
	$("#firstName").val(item.firstName);
	$("#lastName").val(item.lastName);
    $("#streetAddress").val(item.streetAddress);
 	$("#aptAddress").val(item.aptAddress);    
    $("#phone").val(item.phone);
    $("#state").val(item.state);
    $("#city").val(item.city);
    $("#zip").val(item.zip);
    $("#email").val(item.email);
    $("#custId").val(item.custId);  
    $("#editCustId").val(item.custId);        		   
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
	var customerInfo = $("#firstName").val() + " " + $("#lastName").val() + "<br/>";
	var additionalInfo = "";
	if ($("#phone").val().length != 0) {
   		additionalInfo = additionalInfo + "phone: " + $("#phone").val() + ", ";
	}
	if ($("#email").val().length != 0) {
   		additionalInfo = additionalInfo + "email: " + $("#email").val() + "<br/>";
	} else {
		additionalInfo = additionalInfo.substring(0,additionalInfo.length-2) + "<br/>"//remove ", " and add line break if there isn't an email address 
	}
	customerInfo = $(".customerAddress").reduce(customerInfo,addInfo); //reduce address fields to single piece of HTML
	$("#selectedCustomerDiv").html(customerInfo+additionalInfo); //populate customer div with customer information
};

order.popDelivery = function () { //pop delivery calculator div
	$("#deliveryZip").val($("#zip").val()); //prepopulate with customer's zip
	$("#peak").attr("checked",false);
	$("#enterDeliveryZipDialog").dialog({ 
		modal: true, 
		title: "Enter Delivery Zip to Calculate Fee and Tolls",
		width: 400,
		buttons: [ 
			{ text: "Submit", click:function() { 
				if (!($("#enterDeliveryZipForm").validate().form())) {//validate form
					$("#incompleteDeliveryDialog").dialog({ modal: true });
					return;	
				}			
				order.requestDestinationData($("#deliveryZip").val());
				$(this).dialog("close"); 
			}}, 	
				{ 
					text: "Cancel", 
					click:function() {        					 
						$(this).dialog("close"); 
	}}]});	
};

order.popEditCustomer = function (dialogTitle) { //pop up customer edit dialog box
	$("#customerDialog").dialog({ 
		modal: true,
		title: dialogTitle,
		width: 500,
		buttons: [ { 
			text: "Save", 
			click: function() { 
				$("#confirmSaveDialog").dialog({ 
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
    			$("#confirmCancelDialog").dialog({ 
        			modal: true, 
        			buttons: [ 
        				{ 
        					text: "Yes", 
        					click: function() { 
        						$(this).dialog("close");
								$("#customerDialog").dialog("close");
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
	    		$("#confirmClearDialog").dialog(
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
	$("#customerForm input[type=text]").val("");
};

order.validateAndSubmitCustomer = function () { //validate and submit customer dialog via ajax
	if ((!($("#customerForm").validate().form())) || (($("#email").val().length == 0) && ($("#phone").val().length == 0))) {//validate form
		$("#incompleteCustomerDialog").dialog({ modal: true });
		return;	
	}
    $.ajax({ //make ajax call to submit order
        url: "customerUpdate",
        cache: false,
        type: "post",  
        dataType: "json",      
        data: $("#customerForm").serialize(),
        success: function( data ) { //order submission succeeded
        	order.populateCustomerDiv();
        	$("#custId").val(data.custId); //populate customer id on order form with correct customer id
        	$("#editCustomerButton").show();//show edit customer button
        	order.checkForShowSubmitButton();//see whether we should show submit button
        	order.checkForShowCancelButton(); //see whether we should show cancel button
        },
        error: function () { //order submission failed
        	$("#customerSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });	
	$("#customerDialog").dialog("close");
};

order.validateAndSubmit = function () { //validate form data and ajax submit to server
	$("#orderInfo").val($("#orderItems tr:gt(0)").reduce("",order.extractItemData));//put table data into hidden field to be sent to server using reduce and function to pull each row's data
	
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("#orderForm").serialize(),
        success: function( data ) { //order submission succeeded
        	$("#orderSubmittedDialog").html("Order # " + data.orderId + " has been submitted. Final cost is " + formatPrice(totalCost)+".").dialog({ modal: true }); //pop success dialog
            order.resetForm();
        },
        error: function () { //order submission failed
        	$("#orderSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

order.resetForm = function () {
	$("#orderItems tr:gt(0)").remove(); //empty order table except for header
	$("input[type=text]").val(""); //empty all fields
	$("#submitButton").hide();
	$("#editCustomerButton").hide();        	
	$("#selectedCustomerDiv").html("");
	//hide buttons
    $("#addButton").hide();
    $("#editCustomerButton").hide();
    $("#submitButton").button().hide();
    $("#enterDeliveryZipButton").hide();
    //disable delivery fields
    $("#deliveryFee").attr("disabled","true");    
    $("#tollExpense").attr("disabled","true");    
    $("#orderId").val("0")   //set orderId to blank order
    order.defaultLabels(); //reset fieldset and panel labels
    $("#orderItems").hide(); //hide order item table since it's now empty
};

order.confirmCancel = function () { //confirm order cancelation
		$("#confirmCancelDialog").dialog({ 
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
	if ($("#orderId").val() == "0") {
		order.resetForm();
	}
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("#orderForm").serialize()+"&cancelled=" + encodeURIComponent("Cancelled"),//append cancelled parameter to form data
        success: function( data ) { //order submission succeeded
        	$("#orderSubmittedDialog").html("Order # " + data.orderId + " has been cancelled.").dialog({ modal: true }); //pop successful cancellation dialog
    		order.resetForm();        	
        },
        error: function () { //order submission failed
        	$("#orderCancellationFailedDialog").dialog({ modal: true }); //pop failure dialog
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
    	if ($("#"+event.target.id).val().indexOf(".") == -1) { //if we don't already have a decimal point in the number then allow it
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
		var dateArray = $("#deliveryDate").val().split("/"); //split date from deliveryDate field
		var dayOfWeek = new Date(dateArray[2], dateArray[0]-1, dateArray[1]).getDay();
		var deliveryTime = parseInt($("#deliveryTime").val()); //get time from deliveryTime field
		var peak = false;
		if (dayOfWeek == 0 || dayOfWeek == 6) { //this is a weekend date 
			if ((order.compareTime(deliveryTime,"10:00 AM")>0) && (order.compareTime("9:00 PM",deliveryTime)>0)) { // if delivery time is after 10AM in the morning and before 9PM in the evening
				peak = true;
			}
		} else /*weekday*/ if (((order.compareTime(deliveryTime,"5:00 AM")>0) && (order.compareTime("10:00 AM",deliveryTime)>0)) || // if delivery time is between 5AM and 10AM in the morning 
					(order.compareTime(deliveryTime,"3:00 PM")>0) && (order.compareTime("8:00 PM",deliveryTime)>0)) { // or if delivery time is between 3PM and 8PM in the evening
			peak = true;
		}
		$("#deliveryFee").val(formatPrice(destinationInfo.distance * 2.5).substring(1)); 
		if (peak == true) {
			$("#tollExpense").val(10.25);
		} else {
			$("#tollExpense").val(8.25);
		}
	} else if (destinationInfo.County == "Queens County" || destinationInfo.County == "Nassau County" || destinationInfo.County == "Suffolk County" || destinationInfo.County == "Kings County") {
		$("#deliveryFee").val(formatPrice(destinationInfo.distance * 1.5).substring(1));
	} else if (destinationInfo.County == "New York County") {
		$("#deliveryFee").val(formatPrice(destinationInfo.distance * 2.5).substring(1));		
	} else if (destinationInfo.County == "Bronx County" || destinationInfo.County == "Westchester County" || destinationInfo.County == "Richmond County") {
		$("#deliveryFee").val(formatPrice(destinationInfo.distance * 1.5).substring(1));
		$("#tollExpense").val(9.60);		
	}
};
