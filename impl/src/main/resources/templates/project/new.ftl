<@layout>
	<form>
		<div id="error" style="color:red"></div>
		<div class="DTE_Form_Content">
			<div>
				<label class="DTE_Label">
					Name:
				</label>
				<div class="DTE_Field_Input">
					<input id="name" type="text" size="50" required autofocus>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					Description:
				</label>
				<div>
					<textarea id="description" rows="5" cols="50"></textarea>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					SDTM Version:
				</label>
				<div>
					<select id="sdtmVersion">
						<option value="">Select</option>
						<#list sdtmVersions as sdtmVersion>
							<option value="${sdtmVersion.id?long?c}">${sdtmVersion.description}</option>
						</#list>
					</select>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					Control Terminology:
				</label>
				<div>
					<select id="ctSelect">
					</select>
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
							<tr id="tr_0" data_id="0">
								<td><a href="javascript:removeLib(0)"><b>Remove</b></a></td>
								<td><input name="name" type="text" value="SOURCE"></td>
								<th><input name="path" type="text" value="source"></td>
							</tr>
							<tr id="tr_1" data_id="1">
								<td><a href="javascript:removeLib(1)"><b>Remove</b></a></td>
								<td><input name="name" type="text" value="WORK"></td>
								<th><input name="path" type="text" value="work"></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<a class="dt-button buttons-add-library" href="#"><span>Add Library</span></a>
			<a class="dt-button buttons-cancel" href="/project/"><span>Cancel</span></a>
			<a class="dt-button buttons-create" href="#"><span>Create</span></a>
		</div>
	</form>
	<script>
		$(document).ready(function() {
			$("#sdtmVersion").change(function() {
				var id = $("#sdtmVersion").val();
				$("#ctSelect").empty();
				if(id && id!=''){
					$.ajax(
						{
							url: "/sdtm/v1/version/"+id+"/ct", 
							success: function(result){
								result.map(function(row){
			                		$("#ctSelect").append('<option value="'+row.id+'">'+row.name+'</option>');
								});
    						}
    					}
    				);
				}
			});
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
			$("a.buttons-create").click(
				function() {
					var name = $("#name").val();
					var desc = $("#description").val();
					var versionId = $("#sdtmVersion").val();
					var ctId = $("#ctSelect").val();
					var data = {};
					data.name = name;
					if(desc){
						data.description = desc;
					}
					data.ctId = ctId;
					data.versionId = versionId;
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
					//console.log(data);
		            $.ajax({url: "/sdtm/v1/project",contentType:'application/json', type:'POST', data: JSON.stringify(data), success: function(result){
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