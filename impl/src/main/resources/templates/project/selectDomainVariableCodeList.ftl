<@layout>
	<table id="codeList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Code List Name</th>
                <th>Submission Value</th>
                <th>Code Id</th>
                <th>Included</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
		    editor = new $.fn.dataTable.Editor( {
				 ajax: function ( method, url, d, successCallback, errorCallback ) {
		            if ( d.action === 'remove' ) {
		                $.each( d.data, function (id, value) {
		                	var type="POST";
		                	if(value.added){
		                		type="DELETE";
		                	}
		                	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/variable/${variableId?long?c}/codeList/"+value.id,type:type, success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
        	    },
		        idSrc:  'id',
		        table: "#codeList"
		    } );
			editor.on( 'open', function ( e, mode, action ) {
				if(action=='remove'){
                    var data = table.row( { selected: true } ).data();
                    if(data.added){
                    	editor.message("Are you sure you wish to remove <b>"+data.name+"</b>?");
                    }else{
                    	editor.message("Are you sure you wish to add <b>"+data.name+"</b>?");
                    }
				}
			} );
		    
		    var table = $('#codeList').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId?long?c}/variable/${variableId?long?c}/codeListQuery",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name"},
		            { "data": "cdiscsubmissionValue" },
		            { "data": "extCodeId", "defaultContent":"" },
		            { "data": "added"}
		        ],
				"columnDefs": [ 
				  {
				    "targets": 3,
				    "data": "id",
				    "render": function ( data, type, full, meta ) {
				      return data? "Yes":"No";
				    },
				  }

				],
		        select: {
		            style:    'single'
		        },
		        buttons: [
		            { 
		              extend: "remove", 
                	  text: "Add/Remove Code List",
 		              editor: editor,
 		              formButtons: [
                    	'Confirm',
                    	{ label: 'Cancel', fn: function () { this.close(); } }
               		  ]
               		}
		        ]
		    });
		    
		    $("#codeList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
