<@layout>
	<table id="codeList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Submission Value</th>
                <th>Code Id</th>
                <th>Extensible</th>
                <th>Customized</th>
                <th>Operations</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
		    $('#codeList').DataTable( {
		        "aaSorting": [],
		    	"dom": '<"toolbar">frtip',
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/odm/v1/codeListForCT?ctId=${ctId}",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "cdiscsubmissionValue" },
		            { "data": "extCodeId" },
		            { "data": "codeListExtensible" },
		            { "data": "customized" }
		        ],
				"columnDefs": [ 
				  {
				    "targets": 0,
				    "data": "name",
				    "render": function ( data, type, full, meta ) {
				      	if(full.customized){
				      		return '<a href="/ct/${ctId}/customizedCodeList/'+full.id+'">'+data+'</a>';
				      	}
				      	return '<a href="/ct/${ctId}/codeList/'+full.id+'">'+data+'</a>';
				    }				    
				  },
				  {
				    "targets": 4,
				    "data": "customized",
				    "render": function ( data, type, full, meta ) {
				      return data?"Yes":"No";
				    }				    
				  },
				  {
				    "targets": 5,
				    "data": "customized",
				    "render": function ( data, type, full, meta ) {
				      if(data){
				      	return '<a href="/ct/${ctId}/codeList/'+full.id+'?edit=true">Edit</a>&nbsp;&nbsp;<a href="/ct/${ctId}/codeList/'+full.id+'?remove=true">Remove</a>';
				      }else{
				      	return "";
				      }
				    }				    
				  }
				  
				]		        
		    });
		    $("div.toolbar").html('<a href="/ct/${ctId}/codeList/new">New Code List</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="/ct/${ctId}/codeList/select">Add Existing Code List</a>');
		    $("#codeList_wrapper").css("width", "100%");
		} );	
	</script>
</@layout>>
