<div id="rootwizard">
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<ul>
					<li><a href="#tab1" data-toggle="tab">Select Variable</a></li>
					<li><a href="#tab2" data-toggle="tab">Rename Variable</a></li>
					<li><a href="#tab3" data-toggle="tab">Sort Direction</a></li>
					<li><a href="#tab4" data-toggle="tab">Condition</a></li>
					<li><a href="#tab5" data-toggle="tab">Preview</a></li>
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
			<div id="columnsDiv" style="display:none">
				Columns: 
				<select id="column" multiple="multiple">
				</select>
			</div>
			<div id="sortColumnsDiv" style="display:none">
				Sort Columns: 
				<select id="sortColumn" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="tab-pane" id="tab2">
			<table id="aliasTable" style="display:none">
				<thead>
					<tr>
						<td>Name</td>
						<td>Alias</td>
					</tr>
				</thead>
				<tbody id="aliasBody">
				</tbody>
			</table>
		</div>
		<div class="tab-pane" id="tab3">
			<table id="sortTable" style="display:none">
				<thead>
					<tr>
						<td>Name</td>
						<td>Direction</td>
					</tr>
				</thead>
				<tbody id="sortBody">
				</tbody>
			</table>
		</div>
		<div class="tab-pane" id="tab4">
			<textarea id="condition" rows="10" cols="100"></textarea>
		</div>
		<div class="tab-pane" id="tab5">
			<textarea id="preview" rows="10" style="width:100%" readonly="readonly"></textarea>
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
		$('#condition').change(function(e){
    		generatePreview();
		});
	
		$('#library').change(function(e){
			var library = $('#library').val();
			if(library){
				var url = "/sdtm/v1/project/${prjId?long?c}/library/"+library;
				if(library == "-1"){
					url = "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet";
				}
				$.ajax({url: url, success: function(result){
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
				var url = "/sdtm/v1/project/${prjId?long?c}/library/"+library+"/fileName/"+dataSet;
				if(library == "-1"){
					url = "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/"+dataSet;
				}
				$.ajax({url: url, success: function(result){
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
		$('#column').change(function(e){
			var columns = $("option:selected",$('#column'));
			if(columns){
				$('#sortColumn').children().remove();
				$("#aliasBody").children().remove();
	    		if(columns.length > 0){
					columns.map(function(){
					   	var $this = $(this);
						$('#sortColumn').append($("<option></option>").attr("value",$this.val()).text($this.text()));
						var newRowContent = '<tr><td>'+$this.val()+'</td><td><input id="alias_' + $this.val() + '" type="text" onchange="javascript:generatePreview()"/></td></tr>';
						$("#aliasBody").append(newRowContent); 
		    		});
		    		$('#sortColumnsDiv').css("display", "block");
		    		$('#aliasTable').css("display", "block");
	    			$('#sortColumn').attr("size",columns.length > 10?10:columns.length);
	    		}else{
		    		$('#sortColumnsDiv').css("display", "none");
		    		$('#aliasTable').css("display", "none");
	    		}
    		}
    		generatePreview();
		});
		$('#sortColumn').change(function(e){
			var sortColumns = $("option:selected",$('#sortColumn'));
			
			if(sortColumns){
				$("#sortBody").children().remove();
	    		if(sortColumns.length > 0){
					sortColumns.map(function(){
					   	var $this = $(this);
						var newRowContent = '<tr><td>'+$this.val()+'</td><td><select id="direction_' + $this.val() + '" onchange="javascript:generatePreview()"><option selected="selected">asc</option><option>desc</option></select></td></tr>';
						$("#sortBody").append(newRowContent); 
		    		});
		    		$('#sortTable').css("display", "block");
	    		}else{
		    		$('#sortTable').css("display", "none");
	    		}
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
		    	var sql = "proc sort data=" + tableName + " out=" + $("#name").val() + "(keep=" + columns.join(" ") ;
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
			data.name = name;
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
			var joinType = $("#joinType").val();
			if(!name || name ==''){
				$("#error").html("Name is required.");
				return false;
			}
			data.name = name;
			data.libraryId = $("#library").val();
			data.dataSet = $("#dataSet").val();
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
