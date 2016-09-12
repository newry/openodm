<@layout>
	<table id="allProject" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Creator</th>
                <th>UpdatedBy</th>
                <th>CreatedDate</th>
                <th>LastModifiedDate</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
			editor = new $.fn.dataTable.Editor( {
        	   idSrc:  'id',
			   table: "#allProject"
			} );
		    var table = $('#allProject').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "description", "defaultContent": ""},
		            { "data": "creator" },
		            { "data": "updatedBy" },
		            { "data": "dateAdded" },
		            { "data": "dateLastModified" }
		        ],
				"columnDefs": [ 
				  {
				    "targets": 0,
				    "data": "name",
				    "render": function ( data, type, full, meta ) {
				      return '<a href="/project/'+full.id+'">'+data+'</a>';
				    }				    
				  }
				],
		        select: {
		            style:    'single',
		            selector: 'td:not(:first-child)'
		        },
		        buttons: [
		            { 
		              extend: "create", 
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                      	window.location='/project/new';
	                  }
               		},
		            { 
		              extend: "edit", 
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                       	var data = table.row( { selected: true } ).data();
                      	window.location='/project/'+data.id+'/edit';
	                  }
               		},
		            { 
		              extend: "edit", 
		              editor: editor,
                	  text: "Go to Domains",
                      action: function ( e, dt, node, config ) {
                       	var data = table.row( { selected: true } ).data();
                      	window.location='/project/'+data.id+'/toc';
	                  }
               		}
		        ]
		    });
			$("#allProject_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
