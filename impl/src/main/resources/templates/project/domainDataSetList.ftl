<@layout>
	<table id="dataSetList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Action</th>
                <th>Output Dataset</th>
                <th>Input Dataset(s)</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
			editor = new $.fn.dataTable.Editor( {
				ajax: function ( method, url, d, successCallback, errorCallback ) {
		            var output = { data: [] };
		 
		            if ( d.action === 'remove' ) {
		                $.each( d.data, function (id, value) {
		                	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/" +id,type:'DELETE', success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
        	   },
        	   idSrc:  'id',
			   table: "#dataSetList"
			} );
		    var table = $('#dataSetList').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/all",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "joinType" },
		            { "data": "output" },
		            { "data": "input" }
		        ],
		        select: {
		            style:    'single'
		        },
		        buttons: [
		            { 
		              extend: "create", 
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                      	window.location='/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/new';
	                  }
               		},
		            { 
		              extend: "edit", 
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                       	var data = table.row( { selected: true } ).data();
                      	window.location='/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet/'+data.id;
	                  }
               		},
		            { 
		              extend: "remove", 
		              editor: editor,
		              formButtons: [
                    	'Remove',
                    	{ label: 'Cancel', fn: function () { this.close(); } }
               		  ]
               		}
		        ]
		    });
			$("#dataSetList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
