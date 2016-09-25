<@layout>
	<form id="frm" method="POST">
		<div>
			<div id="error" style="color:red"></div>
			<div id="basic">
			    Name: <input id="name" name="name" type="text" autofocus="autofocus" <#if joinType??> onchange="javascript:generatePreview()" </#if>/><br/>
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
					createDataSet();
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
		});
	</script>
</@layout>>
