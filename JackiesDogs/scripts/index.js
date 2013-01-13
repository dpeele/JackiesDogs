$(function () { //onload
	$("#panels").tabs(); //resize main body of page on window resize
    $(window).resize(function() {
        $("#panels").height($(window).height() - ($("#panels").offset().top + 60));
    });
    $(window).resize();	
});

formatPrice = function (n) {//format price with dollar sign and correct decimal places
	 
	   var int = parseInt(n = Math.abs(n).toFixed(c)) + ''; //generate whole dollar part of price 
	   
	   return ("$" + int + "." + Math.abs(n - i).toFixed(c).slice(2)); //return prices
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

(function( $ ) { //jquery reduce function
	$.fn.reduce = function(valueInitial, fnReduce) {
		jQuery.each( $(this), function(i, value) {
			valueInitial = fnReduce.apply(value, [valueInitial, i, value]);
		});
		return valueInitial;
	};
})( jQuery );
