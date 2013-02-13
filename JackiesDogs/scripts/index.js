$(function () { //onload
	$("#panels").tabs({ 
		cache: true, 
		select: handleTabSelect,
		show: resizeTab
	}); //set up main tabbed panel and cache urls	 
	
	//resize main body of page on window resize
    $(window).resize(function() {
        $("#panels").height($(window).height() - ($("#panels").offset().top + 60));
    });	
    $(window).resize();	
    
    switchTabFromHash(); //switch tab if hash was passed

    // For forward and back- handle history
	$.address.externalChange(switchTabFromHash);
});

//switch tab based on tab name from hash
switchTabFromHash = function () {
	var hash = window.location.hash;
	var name = (hash == "") ? "" : hash.substring(5);
	
	var tabNumber=0;	
	switch (name) {
		case "order":
			tabNumber=0;
			break;
		case "search":
			tabNumber=1;
			break;
		case "vendorOrder":
			tabNumber=2;
			break;
		case "vendorSearch":
			tabNumber=3;
			break;
		case "admin":
			tabNumber=4;
			break;						
	}
	$("#panels").tabs( "select" , tabNumber );
};

//when a tab has been activated
resizeTab = function () {
	$(window).resize();
};

//when the tab is selected update the url with the hash
handleTabSelect = function (event,ui) {

	var hash="";
	switch (ui.index) {
		case 0:
			hash="tab=order";
			break;
		case 1:
			hash="tab=search";
			break;
		case 2:
			hash="tab=vendorOrder";
			break;
		case 3:
			hash="tab=vendorSearch";
			break;
		case 4:
			hash="tab=admin";
			break;						
	}
	window.location.hash=hash;
};

getId = function() {
	return ($(this).attr("id"));
};

//create order item object
var Item = function (id, quantity, weight, name, price, billBy, estimatedWeight, description, totalWeight, quantityAvailable, productId, /*optional*/ estimated) {
	this.id = id; 
	this.quantity = quantity; 
	this. weight = parseFloat(weight); 
	this.name = name; 
	this.price = formatPrice(price); 
	this.billBy = billBy; 
	if (estimatedWeight == 0) {
		this.estimatedWeight = 1.0;
	} else {
		this.estimatedWeight = parseFloat(estimatedWeight);
	}
	this.description = description;
	this.totalWeight = totalWeight;
	this.quantityAvailable = quantityAvailable;
	this.productId = productId;
	this.removed = false;
	if (estimated != null) {
		this.estimated = estimated;
	} else {
		this.estimated = (this.isByThePound() && this.weight == 0) ? true : false;
	}
};
//add methods
Item.prototype.remove = function () {
	this.removed = true;
};
Item.prototype.isRemoved = function () {
	return(this.removed);
};
Item.prototype.isByThePound = function () {
	if (this.billBy == "Pound") {
		return (true);
	}
	return (false);
};
Item.prototype.isEstimate = function () {
	return this.estimated;
};
Item.prototype.getFormattedPrice = function () {
	var formattedPrice = "$"+this.price;        	
	if (this.isByThePound()) {
		formattedPrice = formattedPrice+"/lb";
	}	
	return formattedPrice;
};
Item.prototype.getFormattedTotalPrice = function () {
	var formattedPrice = "$" + this.getUnformattedTotalPrice();
	if (this.isEstimate()) {
		formattedPrice = formattedPrice + " (est)";
	}
	return (formattedPrice);
};
Item.prototype.getUnformattedTotalPrice = function () {
	if (this.isByThePound()) { //priced by the pound but not an estimate
		return (formatPrice(this.weight * this.price));
	} else { //not priced by the pound
		return (formatPrice(this.quantity * this.price));
	}
};
Item.prototype.getFormattedQuantityAndWeight = function () {
	if (this.isByThePound()) {
		return (quantity + " (" + weight + "lbs)");
	} else {
		return (quantity);
	}
};

formatPrice = function (n) {//format price with dollar sign and correct decimal places
	 
	   var int = parseInt(n = Math.abs(n).toFixed(c)) + ''; //generate whole dollar part of price 
	   
	   return (int + "." + Math.abs(n - i).toFixed(c).slice(2)); //return prices
};

if (!Array.prototype.reduce) { //if it doesn't already exist, add reduce function to array object
	Array.prototype.reduce = function reduce(accumulator) {
		if (this===null || this===undefined) {
			throw new TypeError("Object is null or undefined");
		}
		
		var index = 0;
		var length = this.length >> 0; //ensure this is an unsigned 32 bit integer
		var current;
		
		if (typeof accumulator !== "function") { // ES5 : "If IsCallable(callbackfn) is false, throw a TypeError exception."
			throw new TypeError("First argument is not callable");
		}
		 
		if(arguments.length < 2) {
			if (length === 0) {
				throw new TypeError("Array length is 0 and no second argument");
			}
				
			current = this[0];
			index=1;
			
		} else {
			current = arguments[1];
		}
		while (index < length) {
			if (index in this) { //array contains this index
				current = accumulator.call(undefined, current, this[index], index, this);
				index++;
			}
		}		    	 
		return current;
	};
}