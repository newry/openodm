<@layout>
	<table id="eiList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Code</th>
                <th>CDISC Submission Value</th>
                <th>CDISC Synonyms</th>
                <th>CDISC Definition</th>
                <th>NCI Preferred Term</th>
                <th>Operations</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
		    $('#eiList').DataTable( {
		        "aaSorting": [],
		    	"dom": '<"toolbar">frtip',
		    	"bLengthChange": false,
		        "ajax": {
		        	"url": <#if customized>"/odm/v1/customizedEnumeratedItem?ctId=${ctId}&codeListId=${codeListId}"<#else>"/odm/v1/enumeratedItem?ctId=${ctId}&codeListId=${codeListId}"</#if>,
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "extCodeId" },
		            { "data": "codedValue" },
		            { "data": "cdiscsynonym" },
		            { "data": "cdiscdefinition" },
		            { "data": "preferredTerm" }
		        ],
				"columnDefs": [ 
				  {
				    "targets": 2,
				    "data": "cdiscsynonym",
				    "render": function ( data, type, full, meta ) {
				      return data?data:"";
				    }				    
				  },
				  {
				    "targets": 3,
				    "data": "cdiscdefinition",
				    "render": function ( data, type, full, meta ) {
				      return data?data:"";
				    }				    
				  },
				  {
				    "targets": 4,
				    "data": "preferredTerm",
				    "render": function ( data, type, full, meta ) {
				      return data?data:"";
				    }				    
				  },
				  {
				    "targets": 5,
				    "data": "customized",
				    "render": function ( data, type, full, meta ) {
				      if(data || full.extended){
				      	return '<a href="/ct/${ctId}/codeList/${codeListId}/enumeratedItem/'+full.id+'?edit=true">Edit</a>&nbsp;&nbsp;<a href="/ct/${ctId}/codeList/${codeListId}/enumeratedItem/'+full.id+'?remove=true">Remove</a>';
				      }else{
				      	return "";
				      }
				    }
				   } 
				]		        
		    });
		    <#if customized>
		    	$("div.toolbar").html('<a href="/ct/${ctId}/codeList/new">New Enumerated Item</a>');
		    <#elseif extended>
		    	$("div.toolbar").html('<a href="/ct/${ctId}/codeList/new">New Enumerated Item</a>');
		    </#if>
		    $("#eiList_wrapper").css("width", "100%");
		} );	
	</script>
</@layout>>
