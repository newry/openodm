<@layout>
	<table id="codeList" class="display" cellspacing="0" width="100%">
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
		    $('#codeList').DataTable( {
		        "ajax": {
		        	"url":"/odm/v1/codeListForCT?ctId=${id}",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "cdiscsubmissionValue" },
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
				      return data;
				    }				    
				  }
				]		        
		    });
		} );	
	</script>
</@layout>>
