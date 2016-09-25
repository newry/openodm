<@layout>
	<form>
		<div class="DTE_Form_Content">
			<div>
				<label class="DTE_Label">
					Name:
				</label>
				<div class="DTE_Field_Input">
					<#if project??>${project.name}</#if>
				</div>
			</div>
			<div>
				<label class="DTE_Label">
					Description:
				</label>
				<div>
					<textarea id="description" rows="5" cols="50" readonly><#if project??>${project.description!""}</#if></textarea>
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
								<th>Name</th>
								<th>Path</th>
							</tr>
						</thead>
						<tbody id="libContent">
							<#if project??>
								<#list project.libraries as lib>
									<tr>
										<td>${lib.name}</td>
										<th>${lib.path}</td>
									</tr>
								</#list>
							</#if>
						</tbody>
					</table>
				</div>
			</div>
			<a class="dt-button buttons-cancel" href="/project/"><span>Cancel</span></a>
		</div>
	</form>
</@layout>