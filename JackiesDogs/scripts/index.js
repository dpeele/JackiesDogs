$(function () { //onload
	$("#panels").tabs({ cache: true }); //set up main tabbed panel and cache urls	 
	
	//resize main body of page on window resize
    $(window).resize(function() {
        $("#panels").height($(window).height() - ($("#panels").offset().top + 60));
    });
    $(window).resize();	

    // For forward and back- handle history
	$.address.externalChange(function(event){
		var name = window.location.hash != "" ? window.location.hash.split("#")[2] : "";
	    $("#tabs").tabs( "select" , $("#tabs a[name="+ name + "]").attr('href') );
	});
	  
	// when the tab is selected update the url with the hash
	$("#tabs").bind("tabsselect", function(event, ui) { 
		$.address.hash(ui.tab.name);
	});
});

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

if (!Array.prototype.reduce) //if it doesn't already exist, add reduce function to array object
{
  Array.prototype.reduce = function(fun /*, initial*/)
  {
    var len = this.length;
    if (typeof fun != "function")
      throw new TypeError();

    // no value to return if no initial value and an empty array
    if (len == 0 && arguments.length == 1)
      throw new TypeError();

    var i = 0;
    if (arguments.length >= 2)
    {
      var rv = arguments[1];
    }
    else
    {
      do
      {
        if (i in this)
        {
          rv = this[i++];
          break;
        }

        // if array contains no values, no initial value to return
        if (++i >= len)
          throw new TypeError();
      }
      while (true);
    }

    for (; i < len; i++)
    {
      if (i in this)
        rv = fun.call(null, rv, this[i], i, this);
    }

    return rv;
  };
}