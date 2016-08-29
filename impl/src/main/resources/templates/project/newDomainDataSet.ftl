<@layout>
	<form id="frm" method="POST">
	<div>
		<div id="basic">
		    Name: <input id="name" name="name" type="text" autofocus="autofocus" ng-required="true"/><br/>
			Library: 
				<select id="storeLibrary" name="storeLibrary">
					<#list libs as lib>
						<option value="${lib.id?long?c}">${lib.name}</option>
					</#list>
				</select>
			<br/>
			Join Type: 
			<select id="joinType" name="joinType">
				<option value="">please select</option>
				<option>sort</option>
				<option>merge</option>
				<option>set</option>
			</select>
		</div>
		<#if joinType??>
			<#if joinType == 'sort'>
				<#include "/project/include/sortDataSet.ftl">
			<#elseif joinType == 'merge'>
				<#include "/project/include/mergeDataSet.ftl">
			<#else>
				<#include "/project/include/setDataSet.ftl">
			</#if>
		</#if>
	</div>
	</form>
	<script>
		$(document).ready(function() {
			var ids = ['rootwizard'];
			ids.map(function(item){
						$('#'+item).bootstrapWizard({onTabShow: function(tab, navigation, index) {
						var $total = navigation.find('li').length;
						var $current = index+1;
						var $percent = ($current/$total) * 100;
						$('#'+item).find('.bar').css({width:$percent+'%'});
						
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
					});
			});
			$('#joinType').change(function(e){
				$('#frm').submit();
   				
			});
			<#if joinType??>
				$('#joinType').val('${joinType}');
			</#if>
			<#if name??>
				$('#name').val('${name}');
			</#if>
			<#if storeLibrary??>
				$('#storeLibrary').val('${storeLibrary}');
			</#if>
			$('#librarySort').change(function(e){
				var librarySort = $('#librarySort').val();
				if(librarySort){
					$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/library/"+librarySort, success: function(result){
						$('#dataSetSort').children().remove();
						result.map(function(row){
							$('#dataSetSort').append($("<option></option>").attr("value",row.name).text(row.name));
	    				});
	    			}});
    			}
			});
			
		});
	</script>
</@layout>>
