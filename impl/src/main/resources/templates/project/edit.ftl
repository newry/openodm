<@layout>
	<form>
		<div id="error" style="color:red"></div>
		<div class="DTE_Form_Content">
			<div>
				<label class="DTE_Label">
					Name:
				</label>
				<div class="DTE_Field_Input">
					<input id="name" type="text" size="50" value="<#if project??>${project.name}</#if>" required autofocus>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					Description:
				</label>
				<div>
					<textarea id="description" rows="5" cols="50"><#if project??>${project.description!""}</#if></textarea>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					Libraries:
				</label>
				<div>
					<table class="table" width="100%">
						<thead>
							<tr>
								<th>Operation</th>
								<th>Name</th>
								<th>Path</th>
							</tr>
						</thead>
						<tbody id="libContent">
							<#if project??>
								<#list project.libraries as lib>
									<tr id="tr_${lib?index}" data_id="${lib?index}">
										<td><a href="javascript:removeLib(${lib?index})"><b>Remove</b></a></td>
										<td><input name="name" type="text" value="${lib.name}"></td>
										<th><input name="path" type="text" value="${lib.path}"></td>
									</tr>
								</#list>
							</#if>
						</tbody>
					</table>
				</div>
			</div>
			<a class="dt-button buttons-add-library" href="#"><span>Add Library</span></a>
			<a class="dt-button buttons-edit" href="#"><span>Update</span></a>
		</div>
	</form>
	<script>
		$(document).ready(function() {
			$("a.buttons-add-library").click(
				function() {
					var node = $("#libContent tr:last-child");
					var id = 0;
					if(node.length > 0){
						var dataId = node.attr("data_id");
						id = parseInt(dataId)+1;
					}
					var newTr = $("<tr id='tr_"+id+"' data_id='"+id+"'><td><a href='javascript:removeLib("+id+")'><b>Remove</b></a></td><td><input name='name' type='text'></td><td><input name='path' type='text'></td></tr>");
					$("#libContent").append(newTr);
				}
			);
			removeLib = function(id){
				$("tr").remove("#tr_"+id);
			}
			$("a.buttons-edit").click(
				function() {
					var name = $("#name").val();
					var desc = $("#description").val();
					var data = {};
					data.name = name;
					if(desc){
						data.description = desc;
					}
					var nameNodes = $("#libContent input[name='name']");
					var pathNodes = $("#libContent input[name='path']");
					if(nameNodes.length > 0){
						var libraryList = new Array();
						for(var i=0;i<nameNodes.length && i < pathNodes.length;i++){
							var name = nameNodes[i].value;
							var path = pathNodes[i].value;
							if(name!='' && path!=''){
								libraryList.push({"name":name, "path":path});
							}
						}
						if(libraryList.length > 0){
							data.libraryList = libraryList;
						}
					}
		            $.ajax({url: "/sdtm/v1/project/${prjId!'0'}",contentType:'application/json', type:'PUT', data: JSON.stringify(data), success: function(result){
		                window.location='/project';
    				},
			        error:function(xhr, error, thrown){
			             $("#error").html(xhr.responseJSON.result.error)
	    			}
    				});
					
				}
			);
		} );	
	</script>
	
</@layout>