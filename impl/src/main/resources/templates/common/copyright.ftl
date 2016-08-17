Copyright 2016.All rights reserved.
<script>
	collapsible('${selected!"home"}', true);
	errorHandler = function(editor, xhr, error, thrown, action){
		if(xhr.responseJSON && xhr.responseJSON.result){
			editor.error(xhr.responseJSON.result.error);
		}else{
			editor.error("General Error");
		}
		editor._processing(false);
		editor._event("submitComplete",[xhr, error, thrown, action]);
	}
	
</script>