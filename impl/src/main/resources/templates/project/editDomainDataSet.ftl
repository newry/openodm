<@layout>
	<div>
		<div id="error" style="color:red"></div>
		<div id="basic">
		    Name: ${dataSet.name} <input id="name" type="hidden" value="${dataSet.name}"/><br/>
			Library: ${dataSet.sdtmProjectLibrary.name} <input id="storeLibrary" type="hidden" value="${dataSet.sdtmProjectLibrary.name}"/><br/>
			Join Type: ${dataSet.joinType}<br/>
		</div>
		<#if dataSet.joinType??>
			<#if dataSet.joinType == 'sort'>
				<#include "/project/include/sortDataSet.ftl">
			<#elseif dataSet.joinType == 'merge'>
				<#include "/project/include/mergeDataSet.ftl">
			<#else>
				<#include "/project/include/setDataSet.ftl">
			</#if>
		</#if>
	</div>
	<script>
		$(document).ready(function() {
			var ids = ['rootwizard'];
			ids.map(function(item){
				$('#'+item).bootstrapWizard({onTabShow: function(tab, navigation, index) {
					var $total = navigation.find('li').length;
					var $current = index+1;
						
					// If it's the last tab then hide the last button and show the finish instead
					if($current >= $total) {
						$('#'+item).find('.pager .next').hide();
						$('#'+item).find('.pager .finish').show();
						$('#'+item).find('.pager .finish').removeClass('disabled');
					} else {
						$('#'+item).find('.pager .next').show();
						$('#'+item).find('.pager .finish').hide();
					}
				}});
				$('#'+item+' .finish').click(function() {
					updateDataSet();
				});
			});
		});
		<#if dataSet.joinType??>
			<#if dataSet.joinType == 'sort'>
				var metaData = ${dataSet.metaData};
				//console.log(metaData);
				if(metaData.libraryId){
					$('#library').val(metaData.libraryId);
					$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/library/"+metaData.libraryId, success: function(result){
						result.map(function(row){
							$('#dataSet').append($("<option></option>").attr("value",row.name).text(row.label));
		    			});
		    			if(result.length > 0){
		    				$('#dataSet').val(result[0].name);
		    				$('#dataSetDiv').css("display", "block");
		    			}else{
		    				$('#dataSetDiv').css("display", "none");
		    			}
						if(metaData.dataSet){
							$('#dataSet').val(metaData.dataSet);
							var columns = new Array();
							var sortColumns = new Array();
							var aliasColumns = new Array();
							var sortOrders = new Array();
							if(metaData.columns){
								for(var i=0;i<metaData.columns.length;i++){
									var column = metaData.columns[i];
									var name = column.originalName? column.originalName: column.name;
									if(column.originalName){
										aliasColumns[column.originalName] = column.name;
									}
									columns.push(name);
									if(column.sortOrder){
										sortColumns.push(name);
										sortOrders[name] = column.sortOrder;
									}
								}
							}
							$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/library/"+metaData.libraryId+"/fileName/"+metaData.dataSet, success: function(result){
								if(result.length > 0){
									result.map(function(row){
										var val = row.name;
										var op = $("<option></option>").attr("value",val).text(val);
										if(columns.includes(val)){
											op.attr("selected","selected");
											var sortOp = $("<option></option>").attr("value",val).text(val);
											var newRowContent = '<tr><td>'+val+'</td><td><input id="alias_' + val + '" type="text" onchange="javascript:generatePreview()"/></td></tr>';
											if(aliasColumns[val]){
												newRowContent = '<tr><td>'+val+'</td><td><input id="alias_' + val + '" type="text" value="' + aliasColumns[val] + '" onchange="javascript:generatePreview()"/></td></tr>';
											}
											$("#aliasBody").append(newRowContent); 
											if(sortColumns.includes(val)){
												if(sortOrders[val]){
													if(sortOrders[val]=='asc'){
														var newAliasRowContent = '<tr><td>'+val+'</td><td><select id="direction_' +val + '" onchange="javascript:generatePreview()"><option selected="selected">asc</option><option>desc</option></select></td></tr>';
														$("#sortBody").append(newAliasRowContent); 
													}else{
														var newAliasRowContent = '<tr><td>'+val+'</td><td><select id="direction_' +val + '" onchange="javascript:generatePreview()"><option>asc</option><option selected="selected">desc</option></select></td></tr>';
														$("#sortBody").append(newAliasRowContent); 
													}
												}
												sortOp.attr("selected","selected");
											}
											$('#sortColumn').append(sortOp);
										}
										$('#column').append(op);
					    			});
				    				$('#columnsDiv').css("display", "block");
					    			$('#column').attr("size",result.length > 10?10:result.length);
						    		$('#sortColumnsDiv').css("display", "block");
						    		$('#aliasTable').css("display", "block");
					    			$('#sortColumn').attr("size",columns.length > 10?10:columns.length);
						    		$('#sortTable').css("display", "block");
						    		if(metaData.condition){
										$('#condition').val(metaData.condition);
									}
									generatePreview();
				    			}
				    		}});
						}
	    			}});
					
				}
			<#elseif dataSet.joinType == 'merge'>
			<#else>
			</#if>
		</#if>
		
	</script>
</@layout>>
