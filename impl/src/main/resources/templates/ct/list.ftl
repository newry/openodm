<@layout>
	<table id="allCT" class="display" cellspacing="0" width="100%">
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
		    $('#allCT').DataTable( {
		        "ajax": {
		        	"url":"/odm/v1/controlTerminology",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "description" },
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
				      return '<a href="/ct/'+full.id+'">'+data+'</a>';
				    }				    
				  },
				  {
				    "targets": 1,
				    "data": null,
				    "defaultContent": ""
				  } 
				]		        
		    });
		} );	
	</script>
</@layout>>
