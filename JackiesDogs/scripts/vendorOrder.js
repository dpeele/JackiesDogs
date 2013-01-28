var vendorOrder = {};                                  

vendorOrder.currentItem = null; //the current item selected by the user
vendorOrder.orderItems = []; //the current list of items in this order


vendorOrder.onload = function () { //called onload of this panel
	 
	vendorOrder.defaultLabels(); //reset fieldset and panel labels
	
    $(window).resize(function() { //resize main body of form on window resize
        $("div#vendorOrderPanel #details").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 75));
        $("div#vendorOrderPanel #leftOrder").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 65));
        $("div#vendorOrderPanel #rightOrder").height($(window).height() - ($("div#vendorOrderPanel #details").offset().top + 65));
    });
    $(window).resize();
    
    //add class ui-widget to all text elements and set their name attribute = to their id attribute
    $("div#vendorOrderPanel :input").addClass("ui-widget").attr("name",$(this).attr("id"));

	//hide hidden fields
	$("div#vendorOrderPanel .hidden").hide();
	
	//hide order item table since it's empty
	$("div#vendorOrderPanel #orderItems").hide();
	
	//select first vendor by default
	$("div#vendorOrderPanel #vendor")[0].selectedIndex = 0;
	$("div#vendorOrderPanel #vendor").change(function () {vendorOrder.toggleProductLookup(); vendorOrder.checkForShowSubmitButton();});

	//set default value of order id to 0
	$("div#vendorOrderPanel #orderId").val("0");	

	//quantity field requires fixed width and handler for keyup
	$("div#vendorOrderPanel #quantity").css("width","75px").keyup(vendorOrder.checkForShowAddButton); //when quantity value changes see whether we should show add button
	
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
    $("div#vendorOrderPanel .cost").change(vendorOrder.setFinalCost);
    
    //dialogs
	$("div#vendorOrderPanel div.dialog").hide(); //set all dialog divs to not be visible
	
	//set value of delivered and personal checkboxes
	$("div#vendorOrderPanel :checkbox").attr("value","true");
    
	//buttons- all hidden initially
    $("div#vendorOrderPanel #addButton").button().attr("value","Add Item").click(function () {vendorOrder.checkQuantity(vendorOrder.addItem,vendorOrder.currentItem, $("div#vendorOrderPanel #quantity").val());}).hide(); //add item to order
    $("div#vendorOrderPanel #submitButton").button().attr("value","Submit Order").click(vendorOrder.validateAndSubmit).hide();//submit order
    $("div#vendorOrderPanel #cancelButton").button().attr("value","Cancel Order").click(vendorOrder.confirmCancel).hide();//cancel order    
    $("div#vendorOrderPanel #enterDeliveryZipButton").button().attr("value","Calculate Delivery").click(vendorOrder.popDelivery).hide();//pop delivery div
    
    //number only fields
    $("div#vendorOrderPanel .onlyNumbers").keydown(vendorOrder.onlyNumbers).css("width","75px"); //only allow numbers
    
    //set discount field widths
    $("div#vendorOrderPanel #discount").css("width","25px");
    $("div#vendorOrderPanel #changeDue").css("width","50px");
    $("div#vendorOrderPanel #discount").css("width","25px");
    
    //decimal number only fields
    $("div#vendorOrderPanel .onlyDecimalNumbers").keydown(vendorOrder.onlyNumbersAndDecimalPoint).css("width","75px"); //only allow numbers and a decimal point
            
    //set field widths
    $("div#vendorOrderPanel #quantity").css("width","35px");
    $("div#vendorOrderPanel #quantityAvailable").css("width","35px");
    $("div#vendorOrderPanel #discount").css("width","25px");
    $("div#vendorOrderPanel #changeDue").css("width","50px");
    $("div#vendorOrderPanel #credit").css("width","50px");
    $("div#vendorOrderPanel #deliveryFee").css("width","50px").attr("disabled","true");    
    $("div#vendorOrderPanel #tollExpense").css("width","50px").attr("disabled","true");        
    
    //date field
    $("div#vendorOrderPanel #deliveryDate").datepicker().css("width","100px").keyup(vendorOrder.checkForDeliveryButton); //create date picker for optional delivery date
    
    //display only fields
    $("div#vendorOrderPanel .readOnly").attr("readonly",true);//don't allow editing of total price or quantity available

    //set default value for display only total fields
    $("div#vendorOrderPanel .total").val("$0.00").css("width","75px");        

    //product autocomplete
    $("div#vendorOrderPanel #item").autocomplete({ //look up product based on name or id
        source: function( request, response ) {
            $.ajax({
                url: "productLookup",                
                dataType: "json",
                cache: false,
                type: "post",
                data: {
                    maxRows: 12,
                    match: request.term,
                    vendorType: $("div#vendorOrderPanel #vendor option:selected").val()
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
        	vendorOrder.currentItem = new Item(0, 0, 0, ui.item.productName, ui.item.price, ui.item.billBy, ui.item.estimatedWeight, ui.item.description, ui.item.totalWeight, ui.item.quantity, ui.item.id);
        	$("div#vendorOrderPanel #price").val(order.getFormattedPrice());
        	$("div#vendorOrderPanel #quantityAvailable").val(vendorOrder.currentItem.quantityAvailable);
        	$("div#vendorOrderPanel #item").val(vendorOrder.currentItem.name);
        	var estimatedWeight = vendorOrder.currentItem.estimatedWeight;
        	if (estimated == 1) {
        		estimatedWeight = estimatedWeight + " lb";
        	} else {
        		estimatedWeight = estimatedWeight + " lbs";
        	}
    		$("div#vendorOrderPanel #estimatedWeight").val(estimatedWeight);             
    		if (vendorOrder.currentItem.description.length > 0) { //add tooltip to table row if description exists
    			$(".item").simpletip({  
    				content: vendorOrder.currentItem.description,
    				fixed: true
    			});
    		vendorOrder.checkForShowAddButton();//see whether we should show add button
        },        
        minLength: 2,
    }); 

};

vendorOrder.toggleProductLookup = function () {
	if $("div#vendorOrderPanel #vendor option:selected").length) { //an option has been selected
		$("div#vendorOrderPanel #item).val("");
		$("div#vendorOrderPanel #item).removeAttr("disabled");		
	} else { //otherwise no vendor has been selected so we disable product lookup and put text in to explain
		$("div#vendorOrderPanel #item).val("Select Vendor");
		$("div#vendorOrderPanel #item).attr("disabled", "disabled"); 
	}
};

vendorOrder.defaultLabels = function () {
	$("#vendorOrderAnchor").html("New Vendor Order"); //default vendor order panel label is New Vendor Order
	$("div#vendorOrderPanel #orderLegend").html("Enter Information:"); //default legend is Enter Information:
	 $("#vendorOrderAnchor").attr("href","loadVendorOrder"); //dafault url for panel is loadOrder
};

vendorOrder.checkForShowSubmitButton = function () {
	if ($("div#vendorOrderPanel #vendor option:selected").length) && ($('#orderItems tr').length > 1)) {
		$("div#vendorOrderPanel #submitButton").show();
	}
};

vendorOrder.checkForShowCancelButton = function () {
	if ($('#orderItems tr').length > 1) {
		$("div#vendorOrderPanel #cancelButton").show();
	}
};

vendorOrder.checkForShowAddButton = function () {
	if ($("div#vendorOrderPanel #item").val().length > 0) {
		$("div#vendorOrderPanel #addButton").show();
	}
};

vendorOrder.setFinalCost = function () {
	var totalCost = $("div#vendorOrderPanel #totalCost").val();
	var finalCost = totalCost - ((totalCost / 100) * $("div#vendorOrderPanel #discount").val());  //subtract food discount from order total
	finalCost = finalCost - $("div#vendorOrderPanel #credit").val() + $("div#vendorOrderPanel #deliveryFee").val() + $("div#vendorOrderPanel #tollExpense").val(); //add other costs and subtract credit from order total	
	$("div#vendorOrderPanel #finalCost").val(finalCost);
}

vendorOrder.setFloatValue = function (value, name) { //if the float value isn't zero, set field name to formatted value
	if (parseFloat(value != 0)) {
		$("div#vendorOrderPanel #"+name).val(formatPrice(value).substring(1));
	}
}

vendorOrder.setValues = function (id,deliveryDate,discount,credit,deliveryFee,tollExpense,totalCost,totalWeight,status, mileage) {
	var totalFoodCost = totalCost + ((totalCost / 100) * discount);  //add food discount to order total
	totalFoodCost = "$" + (totalCost + credit - deliveryFee - tollExpense); //subtract other costs and add credit to order total to give us the total of just the food
	$("div#vendorOrderPanel #totalCost").val(formatPrice(totalFoodCost)); //set total food cost
	$("div#vendorOrderPanel #finalCost").val(formatPrice(totalCost).substring(1)); //set final cost	
	$("div#vendorOrderPanel #totalWeight").val(totalWeight); //set total weight	
	if (parseInt(credit) != 0) { //if there is a credit
		$("div#vendorOrderPanel #credit").val(credit);
	}
	vendorOrder.setFloatValue(discount,"discount"); //set the double values
	vendorOrder.setFloatValue(deliveryFee,"deliveryFee");
	vendorOrder.setFloatValue(tollExpense,"tollExpense");
	vendorOrder.setFloatValue(changeDue,"changeDue");
	$("div#vendorOrderPanel select option[value='"+status+"']").attr("selected","selected"); //select the correct status option
	$("div#vendorOrderPanel #orderId").val(id); //set order id
	$("div#vendorOrderPanel #mileage").val(mileage); //set mileage
	$("div#vendorOrderPanel #deliveryDate").val(deliveryDate); //set delivery date
	
	//show buttons where appropriate
	vendorOrder.checkForShowSubmitButton();
	vendorOrder.checkForShowCancelButton();		
	vendorOrder.checkForDeliveryButton();
	$("div#vendorOrderPanel #orderAnchor").html("Edit Vendor Order"); //change order panel label to Edit Vendor Order 
	$("div#vendorOrderPanel #orderLegend").html("Edit Information:"); //change legend to Edit Information:	
};

vendorOrder.checkForDeliveryButton = function () {
	if ($("div#vendorOrderPanel #deliveryDate").val().length > 0) {
		$("div#vendorOrderPanel #enterDeliveryZipButton").show();
        $("div#vendorOrderPanel #deliveryFee").attr("disabled","false");    
        $("div#vendorOrderPanel #tollExpense").attr("disabled","false");  
	}	
};

vendorOrder.checkQuantity = function (destinationFunction, item, quantity) { //check quantity ordered vs quantity in stock and confirm if quantity ordered is greater
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
vendorOrder.updateTotalQuantity = function (item, newQuantity) {
	//get current total weight
	var totalWeight = parseFloat($("div#vendorOrderPanel #totalWeight").val());
	//get old weight				
	var oldWeight = item.weight;
	//get old cost for item
	var oldCost = item.getUnformattedTotalPrice();
	// set quantity of this item to new quantity
	item.quantity = newQuantity;
	// set weight of this item to new weight
	item.weight = parseFloat(item.quantity * item.estimatedWeight);
	//set weight in item table
	$("div#vendorOrderPanel #totalWeight").val(totalWeight - oldWeight + item.weight);
	//get previous total food cost
	var totalCost = parseFloat($("div#vendorOrderPanel #totalCost").val();
	//get new cost for item
	var newCost = item.getUnformattedTotalPrice();
	$("div#vendorOrderPanel #totalCost").val("$"+formatPrice(totalCost-oldCost+newCost));//update total
	$("div#vendorOrderPanel #totalItemPrice"+item.productId).html(item.getFormattedTotalPrice());
}

//update total quantity, total weight, item quantity, and item weight
vendorOrder.updateTotalQuantityAndWeight = function (item, newWeight) {
	item.estimated = false;
	// remove weight of this item from total weight and add in new weight
	var totalWeight = parseFloat($("div#vendorOrderPanel #totalWeight").val());
	//get old cost for item
	var oldCost = item.getUnformattedTotalPrice();
	// set weight of this item to new weight
	item.weight = newWeight;
	//set weight in item table
	$("div#vendorOrderPanel #totalWeight").val(totalWeight - oldWeight + item.weight);
	// set quantity of this item to new quantity
	item.quantity = $("div#vendorOrderPanel #exactQuantity").val();						
	//set quantity in item table
	$("div#vendorOrderPanel #quantity"+item.productId).val(item.getFormattedQuantityAndWeight());
	//get previous total food cost
	var totalCost = parseFloat($("div#vendorOrderPanel #totalCost").val();
	//get new cost for item
	var newCost = item.getUnformattedTotalPrice();
	$("div#vendorOrderPanel #totalCost").val("$"+formatPrice(totalCost-oldCost+newCost));//update total
	$("div#vendorOrderPanel #totalItemPrice"+item.productId).html(item.getFormattedTotalPrice());
}

vendorOrder.addItem = function (item, quantity) { //add item to order
	if ($("div#vendorOrderPanel #orderItems tr").length == 1) {
		$("div#vendorOrderPanel #vendor")..attr("disabled","true");
		$("div#vendorOrderPanel #orderItems").show();
	}
	if (quantity != null) {//new item passed from form, not existing item passed from loaded vendorOrder	
		if (vendorOrder.vendorOrderItems[item.productId] != null) { //this item is already in the table
			$("div#vendorOrderPanel #itemAlreadyAddedDialog").dialog({ modal: true });
			return;				
		}
		item.quantity = quantity;		
		item.weight = parseFloat(item.quantity * item.estimatedWeight);	
		vendorOrder.vendorOrderItems[item.productId] = item;
		vendorOrder.currentItem = {};
	}
	$("div#vendorOrderPanel #orderItems tr:last").after("<tr>\n" + //add row
										"<td id='productId"+item.productId+"'>"+item.productId+"</td>\n" +
										"<td>"+item.productId+"</td>\n" +
										"<td>"+item.name+"</td>\n" +
										"<td><input type='text' id='quantity"+item.productId+"' value='"+item.getFormattedQuantityAndWeight()+"'/>"</td>\n" +
										"<td>"+item.getFormattedPrice()+"</td>\n" +
										"<td id='totalItemPrice"+item.productId+"'>"+item.getFormattedTotalPrice() + "</td>\n" +
										"<td><input type='button' id='button"+item.productId+"'/></td>\n" +
									"</tr>\n");
		
	$("div#vendorOrderPanel #quantity"+item.productId).css("width","100px") //set width of quantity input
	$("div#vendorOrderPanel #item.productId"+item.productId).hide(); //hide td holding item.productId		
	if (item.description.length > 0) { //add tooltip to table row if description exists
		$("div#vendorOrderPanel #orderItems tr:last").simpletip({  
			content: description,
			fixed: true
		});
	}	
		
	if (item.isByThePound()) { //priced by the pound, add focus event handler to pop up estimated dialog and handle that
		$("div#vendorOrderPanel #quantity"+item.productId).focus(function() {
			$("div#vendorOrderPanel #exactQuantity").val(item.quantity);//set initial quantity value in dialog to value from table row
			var oldWeight = item.weight; //get old weight
			$("div#vendorOrderPanel #exactWeight").val(oldWeight);//set initial weight value in dialog to value from table row
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
						var newWeight = parseFloat($("div#vendorOrderPanel #exactWeight").val());
						if (newWeight > item.totalWeight) {
							$("div#vendorOrderPanel #quantityOverrideDialog").dialog({ 
								modal: true, 
								buttons: [ 
									{ text: "Continue", click:function() { 
										$(this).dialog("close");
										vendorOrder.UpdateTotalQuantityAndWeight(item, newWeight);		
										$(this).dialog("close");										
									}}, 	
									{ text: "Cancel", click:function() {        					 
										$(this).dialog( "close" ); 
							}}]});		
						} else {
							vendorOrder.UpdateTotalQuantityAndWeight(item, newWeight);
							$(this).dialog("close");							
						}
					}}, 	
						{ 
							text: "Cancel", 
							click:function() {        					 
								$(this).dialog("close"); 
			}}]});
		});	
	} else { //not estimate, set width and add keydown handler and adjust price as they type
		$("div#vendorOrderPanel #quantity"+item.productId).keydown(function(event){ //add keydown event handler to quantity text box to allow 
			//only numbers and a decimal point and update price on quantity change
			if(vendorOrder.onlyNumbers(event)) {//value may have changed, update total price
				// remove weight of this item from total weight and add in new weight
				var totalWeight = parseFloat($("div#vendorOrderPanel #totalWeight").val());
				//get old weight
				var oldWeight = item.weight;
				//get old cost for item
				var oldCost = item.getUnformattedTotalPrice();
				// set quantity of this item to new quantity
				item.quantity = $("div#vendorOrderPanel #quantity"+item.productId).val();
				// set weight of this item to new weight
				item.weight = parseFloat(item.quantity * item.estimatedWeight);
				//set weight in item table
				$("div#vendorOrderPanel #totalWeight").val(totalWeight - oldWeight + item.weight);
				//get previous total food cost
				var totalCost = parseFloat($("div#vendorOrderPanel #totalCost").val();
				//get new cost for item
				var newCost = item.getUnformattedTotalPrice();
				$("div#vendorOrderPanel #totalCost").val("$"+formatPrice(totalCost-oldCost+newCost));//update total
				$("div#vendorOrderPanel #totalItemPrice"+item.productId).html(item.getFormattedTotalPrice());
			}
		});	
	}
				            			
	$("div#vendorOrderPanel #button"+item.productId).button().attr("value","Remove").click(function(){ //add click event handler to remove button for this row
		$(this).closest('tr').hide();  //remove row
		$("div#vendorOrderPanel #removed"+rowValue).val("true");
		if ($("div#vendorOrderPanel #orderItems tr").length == 1) { //hide table if it's empty
			$("div#vendorOrderPanel #vendor").removeAttr("disabled");
			$("div#vendorOrderPanel #orderItems").hide();
		}	
		//get weight for item
		var weight = item.weight;
		//get cost for item
		var cost = item.getUnformattedTotalPrice();
		//get total weight 
		var totalWeight = parseFloat($("div#vendorOrderPanel #totalWeight").val());
		//get total cost
		var totalCost = parseFloat($("div#vendorOrderPanel #totalCost").val();
		//remove cost of this item from total food cost
		$("div#vendorOrderPanel #totalCost").val("$"+formatPrice(totalCost - cost));//update total
		// remove weight of this item from total weight
		$("div#vendorOrderPanel #totalWeight").val(totalWeight-weight);
		item.remove();//set remove flag of item in array
	});//remove item from order
	$("div#vendorOrderPanel .item").val(""); //reset item fields
	$("div#vendorOrderPanel #addButton").hide();
	vendorOrder.checkForShowSubmitButton(); //see whether we should show submit button
	vendorOrder.checkForShowCancelButton(); //see whether we should show cancel button
	
	if ($("div#vendorOrderPanel #totalCost").val() != "") { // update total price and weight
		//get total cost
		var totalCost = parseFloat($("div#vendorOrderPanel #totalCost").val();
		//get total weight 
		var totalWeight = parseFloat($("div#vendorOrderPanel #totalWeight").val());
		//get cost for item
		var cost = item.getUnformattedTotalPrice();
		//get weight for item
		var weight = item.weight;
		$("div#vendorOrderPanel #totalCost").val("$"+formatPrice(totalCost+cost));
		$("div#vendorOrderPanel #totalWeight").val(totalWeight+item.weight);
	} else { //set price and weight
		$("div#vendorOrderPanel #totalCost").val(item.getformattedTotalPrice());
		$("div#vendorOrderPanel #totalWeight").val(item.weight);
	}	
};

vendorOrder.validateAndSubmit = function () { //validate form data and ajax submit to server
	var orderItemQueryString = escape(order.orderItems.reduce(ExtractItemData));
	
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: orderItemQueryString + $("div#vendorOrderPanel #orderForm").serialize(),
        success: function( data ) { //order submission succeeded
        	$("div#vendorOrderPanel #orderSubmittedDialog").html("Vendor order # " + data.orderId + " has been saved. Estimated cost is " + formatPrice(totalCost)+".").dialog({ modal: true }); //pop success dialog
        	vendorOrder.updateOrderItemIds(data.newOrderItems);
        	$("div#vendorOrderPanel #orderId").val(data.orderId); //set order id

        },
        error: function () { //order submission failed
        	$("div#vendorOrderPanel #orderSubmissionFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

//set the id of any orderItems that were new on this submit 
vendorOrder.updateOrderItemIds = function (newOrderItems) {
	for (orderItem in newOrderItems) {
		vendorOrder.orderItems[orderItem.product.id].id = orderItem.id;
	}
};

vendorOrder.resetForm = function () {
	$("div#vendorOrderPanel #orderItems tr:gt(0)").remove(); //empty order table except for header
	$("div#vendorOrderPanel input[type=text]").val(""); //empty all fields
	$("div#vendorOrderPanel #submitButton").hide();
	//hide buttons
    $("div#vendorOrderPanel #addButton").hide();
    $("div#vendorOrderPanel #submitButton").button().hide();
    $("div#vendorOrderPanel #enterDeliveryZipButton").hide();
    //disable delivery fields
    $("div#vendorOrderPanel #deliveryFee").attr("disabled","true");    
    $("div#vendorOrderPanel #tollExpense").attr("disabled","true");    
    $("div#vendorOrderPanel #orderId").val("0")   //set orderId to blank order
    vendorOrder.defaultLabels(); //reset fieldset and panel labels
    $("div#vendorOrderPanel #orderItems").hide(); //hide order item table since it's now empty
    order.orderItems = [];
};

vendorOrder.confirmCancel = function () { //confirm order cancelation
		$("div#vendorOrderPanel #confirmCancelDialog").dialog({ 
			modal: true, 
			buttons: [ 
				{ text: "Yes", click:function() { 
					$(this).dialog("close");
					vendorOrder.cancelOrder();									
				}}, 	
					{ text: "No", click:function() {        					 
					$(this).dialog("close"); 
		}}]});	
};

vendorOrder.cancelOrder = function () { //cancel order- submit to server if it is an existing order that must be updated in the database
	if ($("div#vendorOrderPanel #orderId").val() == "0") {
		vendorOrder.resetForm();
		return;
	}
    $.ajax({ //make ajax call to submit order
        url: "submitOrder",
        cache: false,
        type: "post",        
        data: $("div#vendorOrderPanel #orderForm").serialize()+"&cancelled=" + encodeURIComponent("Cancelled"),//append cancelled parameter to form data
        success: function( data ) { //order cancel succeeded
        	$("div#vendorOrderPanel #orderSubmittedDialog").html("Order # " + data.orderId + " has been cancelled.").dialog({ modal: true }); //pop successful cancellation dialog
    		vendorOrder.resetForm();        	
        },
        error: function () { //order cancel failed
        	$("div#vendorOrderPanel #orderCancellationFailedDialog").dialog({ modal: true }); //pop failure dialog
        }
    });
};

order.addOrderItems = function () { //add all items of existing order
	for (item in order.orderItems) {
		order.addItem(item);
	}
};

vendorOrder.extractItemData = function (string) {//retrieve data from order array element and add it to string
	if (this.isRemoved() && this.id == 0) {
		return string;
	}
	} else {
		return (string+"items=id="+this.productId+"#quantity="+this.quantity+
				+"#dbId="+this.id+"#removed="+$(this).isRemoved()+"#estimate="+$(this).isEstimate()+"#weight="+$(this).weight+"&");
	}
};

vendorOrder.onlyNumbersAndDecimalPoint = function (event) { //only allow numbers to be entered in this text input
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

vendorOrder.onlyNumbers = function (event) { //only allow numbers to be entered in this text input
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

vendorOrder.requestDestinationData = function (zip) {
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

vendorOrder.populateDeliveryInfo = function (destinationInfo) {
	if (destinationInfo.state == "CT") { //determine mileage, delivery fee, and toll based on distance, county, and state
		$("div#vendorOrderPanel #mileage").val(destinationInfo.distance);
		$("div#vendorOrderPanel #deliveryFee").val("25.00"); 
		$("div#vendorOrderPanel #tollExpense").val("9.60");
	}
};