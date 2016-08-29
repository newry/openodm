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
			    	<div id="dataSetDiv">
						DataSet: 
						<select id="dataSet">
						</select>
					</div>
			    </div>
			    <div class="tab-pane" id="tab2">
			      2
			    </div>
				<div class="tab-pane" id="tab3">
					3
			    </div>
				<div class="tab-pane" id="tab4">
					4
			    </div>
				<div class="tab-pane" id="tab5">
					5
			    </div>
				<ul class="pager wizard">
					<li class="previous"><a href="javascript:;">Previous</a></li>
				  	<li class="next"><a href="javascript:;">Next</a></li>
					<li class="next finish" style="display:none;"><a href="javascript:;">Finish</a></li>
				</ul>
			</div>	
		</div>
