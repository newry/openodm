
window.collapsible = function(zap, alwaysShow) {
	var obj = $("#"+zap);
    if (obj)
    {
    	if(!alwaysShow && obj.hasClass('visible')){
	        obj.removeClass("visible");
    	}else{
	        var visELs = $('ul.visible');
    		visELs.removeClass("visible");
	        obj.addClass("visible");
    	}
        return false;
    }
    else
        return true;
}
function contains(a, obj) {
    var i = a.length;
    while (i--) {
       if (a[i] === obj) {
           return true;
       }
    }
    return false;
}
