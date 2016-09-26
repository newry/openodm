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
			<div id="dataSetDiv">
			    <div class="col-sm-5">
			        <select name="from" id="dataSet" class="form-control" size="8" multiple="multiple">
			        	<#if availableDataSetMap??>
					        <#list availableDataSetMap?keys as key>
					            <optgroup label="${key}">
					            	${availableDataSetMap[key]}
					            </optgroup>
							</#list>
						</#if>
			        </select>
			    </div>
			    
			    <div class="col-sm-2">
			        <button type="button" id="dataSet_rightAll" class="btn btn-block"><i class="glyphicon glyphicon-forward"></i></button>
			        <button type="button" id="dataSet_rightSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
			        <button type="button" id="dataSet_leftSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>
			        <button type="button" id="dataSet_leftAll" class="btn btn-block"><i class="glyphicon glyphicon-backward"></i></button>
			    </div>
			    
			    <div class="col-sm-5">
			        <select name="to" id="dataSet_to" class="form-control" size="8" multiple="multiple">
			        	<#if selectedDataSetMap??>
					        <#list selectedDataSetMap?keys as key>
					            <optgroup label="${key}">
					            	${selectedDataSetMap[key]}
					            </optgroup>
							</#list>
						</#if>
			        </select>
			    </div>
			</div>
		</div>
		<div class="tab-pane" id="tab2">
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
		$("#dataSet").multiselect({
			afterMoveToRight: generatePreview
		});
	});
	var data = {};
	function generatePreview(){
		var dataSetList  =$("#dataSet_to option");
		$("#preview").val('');
		if(dataSetList && dataSetList.length > 0){
			var list = new Array();
			var sql = "data out;\n"
			sql+="set";
		    for(var i=0;i<dataSetList.length;i++){
		    	var dataSet = dataSetList[i].value;
		    	var dataSetName = dataSetList[i].label;
		    	var libId  = dataSetList[i].getAttribute("libraryId");
		    	var libName  = dataSetList[i].getAttribute("LibraryName");
		    	var obj = {};
		    	obj.dataSet=dataSet;
		    	obj.libId=libId;
		        list.push(obj);
		        sql += (" "+libName+"."+dataSetName)
		    }
		    sql+="\n";
		    data.dataSetList = list;
		    sql +="run;";
	        $("#preview").val(sql);
			   		
		}
	}
	<#if dataSetId??>
		updateDataSet = function(){
			var name = $("#name").val();
			if(!name || name ==''){
				$("#error").html("Name is required.");
				return false;
			}
			data.name = name;
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
