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
					Dictionary:
				</label>
				<div>
					<select id="dictionary">
						<#list ctVersions as ctVersion>
							<option value="${ctVersion.id}">${ctVersion.name}</option>
						</#list>
					</select>
				</div>
			</div>
			<a class="dt-button buttons-create" href="#"><span>Create</span></a>
		</div>
	</form>
	<script>
		$(document).ready(function() {
			$("a.buttons-create").click(
				function() {
					var name = $("#name").val();
					var desc = $("#description").val();
					var ctVersionId = $("#dictionary").val();
					var data = {};
					data.name = name;
					if(desc){
						data.description = desc;
					}
					data.ctVersionId = ctVersionId;
		            $.ajax({url: "/odm/v1/controlTerminology",contentType:'application/json', type:'POST', data: JSON.stringify(data), success: function(result){
		                		window.location='/ct';
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