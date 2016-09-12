<@layout>
	<table id="dataSetList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Library</th>
                <th>joinType</th>
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
			   table: "#dataSetList"
			} );
		    var table = $('#dataSetList').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/dataSet",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "sdtmProjectLibrary.name", "defaultContent": ""},
		            { "data": "joinType"},
		            { "data": "creator" },
		            { "data": "updatedBy" },
		            { "data": "dateAdded" },
		            { "data": "dateLastModified" }
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
               		}
		        ]
		    });
			$("#dataSetList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
