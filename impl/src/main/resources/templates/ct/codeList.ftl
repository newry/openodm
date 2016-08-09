<@layout>
	<table id="codeList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Submission Value</th>
                <th>Code Id</th>
                <th>Extensible</th>
                <th>Sponsored</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
		    editor = new $.fn.dataTable.Editor( {
		        ajax: "../php/staff.php",
		        idSrc:  'id',
		        table: "#codeList",
		        fields: [ {
		                label: "Name:",
		                name: "name"
		            }
		        ]
		    } );
		    $('#codeList').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/odm/v1/codeListForCT?ctId=${ctId}",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "description" },
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
				    },
				    "createdCell": function (td, cellData, rowData, row, col) {
					      if ( rowData.customized ) {
					        $(td).addClass('selectable')
					      }
				    }
				  },
				  {
				    "targets": 5,
				    "data": "customized",
				    "render": function ( data, type, full, meta ) {
				      return data?"Yes":"No";
				    }				    
				  }
				],
				select: {
					style:    'single',
					selector: '.selectable'
				},
		        buttons: [
		            { extend: "create", editor: editor },
		            { extend: "edit",   editor: editor },
		            { extend: "remove", editor: editor }
		        ]
		    });
		    $("#codeList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
