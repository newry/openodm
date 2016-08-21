
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
