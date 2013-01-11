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

(function( $ ) { //reduce function
	$.fn.reduce = function(valueInitial, fnReduce) {
		jQuery.each( $(this), function(i, value) {
			valueInitial = fnReduce.apply(value, [valueInitial, i, value]);
		});
		return valueInitial;
	};
})( jQuery );
