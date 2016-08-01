window.collapsible = function(zap, alwaysShow) {
	var obj = $("#"+zap);
    if (obj)
    {
    	if(!alwaysShow && obj.hasClass('visible')){
	        obj.removeClass("visible");
    	}else{
	        var visDivs = $('.visible');
	        for(var i = 0; i < visDivs.length; i++){
	            visDivs[i].removeClass("visble");
	        }
	        obj.addClass("visible");
    	}
        return false;
    }
    else
        return true;
}
