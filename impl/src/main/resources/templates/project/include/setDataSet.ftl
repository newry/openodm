<div id="rootwizard">
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<ul>
					<li><a href="#tab1" data-toggle="tab">Select Variable</a></li>
					<li><a href="#tab2" data-toggle="tab">Preview</a></li>
				</ul>
			 </div>
		</div>
	</div>
	<div class="tab-content">
		<div class="tab-pane" id="tab1">
			<div id="libraryDiv">
				Library: 
				<select id="library">
					<option value="">please select</option>
						<#list libs as lib>
							<option value="${lib.id?long?c}">${lib.name}</option>
						</#list>
				</select>
			</div>
			<div id="dataSetDiv" style="display:none">
				DataSet: 
				<select id="dataSet">
				</select>
			</div>
		</div>
		<div class="tab-pane" id="tab2">
			tab2
		</div>
		<ul class="pager wizard">
			<li class="previous"><a href="javascript:;">Previous</a></li>
			<li class="next"><a href="javascript:;">Next</a></li>
			<li class="next finish" style="display:none;"><a href="javascript:;">Finish</a></li>
		</ul>
	</div>	
</div>
<script>
	$(document).ready(function() {
		$('#library').change(function(e){
			var library = $('#library').val();
			if(library){
				$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/library/"+library, success: function(result){
					$('#dataSet').children().remove();
					result.map(function(row){
						$('#dataSet').append($("<option></option>").attr("value",row.name).text(row.label));
	    			});
	    			if(result.length > 0){
	    				$('#dataSet').val(result[0].name);
	    				$('#dataSetDiv').css("display", "block");
	    			}else{
	    				$('#dataSetDiv').css("display", "none");
	    			}
	    			$('#dataSet').trigger("change");
	    		}});
    		}
    		generatePreview();
		});
		$('#dataSet').change(function(e){
			var library = $('#library').val();
			var dataSet = $('#dataSet').val();
			if(library && dataSet){
				$('#column').children().remove();
				$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/library/"+library+"/fileName/"+dataSet, success: function(result){
					if(result.length > 0){
						result.map(function(row){
							var val = row.name;
							$('#column').append($("<option></option>").attr("value",val).text(val));
		    			});
	    				$('#columnsDiv').css("display", "block");
		    			$('#column').attr("size",result.length > 10?10:result.length);
	    			}else{
	    				$('#columnsDiv').css("display", "none");
	    			}
	    			$('#column').trigger("change");
	    		}});
    		}else{
				$('#column').children().remove();
	    		$('#columnsDiv').css("display", "none");
				$('#sortColumn').children().remove();
	    		$('#sortColumnsDiv').css("display", "none");
	    		$('#column').trigger("change");
    		}
    		generatePreview();
		});
	});
	var data = {};
	function generatePreview(){
		var library = $('#library').val();
		var dataSet = $('#dataSet').val();
		$("#preview").val('');
		if(library && dataSet){
			var columns = new Array();
    		var aliasColumns = new Array();
    		var tableName = $("#library option:selected").text() + "." + $("#dataSet option:selected").text();
    		var selectedColumns = $("option:selected",$('#column'));
            if(selectedColumns && selectedColumns.length > 0){
		    	for(var i=0;i<selectedColumns.length;i++){
		    		var col = selectedColumns[i].value;
		    	    columns.push(col);
		    	    var alias = $("#alias_"+col).val();
		    	    if(alias && alias!=''){
		    	    	aliasColumns.push(col+"="+alias);
		    	    }
		        }
		    	data.columns = columns;
		    	data.aliasColumns = aliasColumns;
		    	<#if dataSetId??>
		    		var storeLibName = $("#storeLibrary").val();
				<#else>
		    		var storeLibName = $("#storeLibrary option:selected").text();
		    	</#if>
		    	var sql = "proc sort data=" + tableName + " out=" + storeLibName+"."+$("#name").val() + "(keep=" + columns.join(" ") ;
	    	    if(aliasColumns.length > 0){
	    	    	sql += " rename=(" + aliasColumns.join(" ") + ")";
	    	    }
	    	    sql += ");\n";
	    		var selectedSortColumns = $("option:selected",$('#sortColumn'));
	            if(selectedSortColumns && selectedSortColumns.length > 0){
	        		var sortColumns = new Array();
		    	    for(var k=0;k<selectedSortColumns.length;k++){
		    	    	var col = selectedSortColumns[k].value;
		    	     	var sortDirection = $("#direction_"+col).val();
		    	        if(sortDirection && sortDirection=='desc'){
			    	    	sortColumns.push("descending "+col);
		    	        }else{
			    	     	sortColumns.push(col);
		    	        }
		        	}
		    		data.sortColumns = sortColumns;
		    	    sql+="by "+sortColumns.join(" ") + ";\n";
	    	    }
	    	    var condition = $("#condition").val();
	            if(condition && condition!=''){
		    		sql+="where "+condition + ";\n";
		    		data.condition = condition;
	            }
	            sql +="run;";
	            $("#preview").val(sql);
			}    		
		}
	}
	<#if dataSetId??>
		updateDataSet = function(){
			var name = $("#name").val();
			data.libraryId = $("#library").val();
			data.dataSet = $("#dataSet").val();
			generatePreview();
			data.sql = $("#preview").val();
			//console.log(data);
			$.ajax(
				{url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/${dataSetId?long?c}",contentType:'application/json', type:'PUT', data: JSON.stringify(data), success: function(result){
					window.location='/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet';
	    		},
				error:function(xhr, error, thrown){
					$("#error").html(xhr.responseJSON.result.error)
		    	}
	    	});
	
		}
	<#else>
		createDataSet = function(){
			var name = $("#name").val();
			var storelibrary = $("#storeLibrary").val();
			var joinType = $("#joinType").val();
			if(!name || name ==''){
				$("#error").html("Name is required.");
				return false;
			}
			data.name = name;
			data.libraryId = $("#library").val();
			data.dataSet = $("#dataSet").val();
			data.storeLibraryId = storelibrary;
			data.joinType = joinType;
			generatePreview();
			data.sql = $("#preview").val();
			//console.log(data);
			$.ajax(
				{url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet",contentType:'application/json', type:'POST', data: JSON.stringify(data), success: function(result){
					window.location='/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet';
	    		},
				error:function(xhr, error, thrown){
					$("#error").html(xhr.responseJSON.result.error)
		    	}
	    	});
	
		}
	</#if>
</script>
