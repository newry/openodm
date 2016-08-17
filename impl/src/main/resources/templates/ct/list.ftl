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
			editor = new $.fn.dataTable.Editor( {
				ajax: function ( method, url, d, successCallback, errorCallback ) {
		            var output = { data: [] };
		 
		            if ( d.action === 'create' ) {
		                $.each( d.data, function (key, value) {
		                	$.ajax({url: "/odm/v1/controlTerminology",contentType:'application/json', type:'POST', data: JSON.stringify(value), success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}
    						});
		                } );
		            }
		            else if ( d.action === 'edit' ) {
		                // Update each edited item with the data submitted
		                $.each( d.data, function (id, value) {
		                	$.ajax({url: "/odm/v1/controlTerminology/"+id,contentType:'application/json', type:'PUT', data: JSON.stringify(value), success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
		            else if ( d.action === 'remove' ) {
		                $.each( d.data, function (id, value) {
		                	$.ajax({url: "/odm/v1/controlTerminology/"+id,type:'DELETE', success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
		            // Show Editor what has changed
		            //successCallback( output );
        	   },
        	   idSrc:  'id',
			   table: "#allCT",
			   fields: [ 
			   		{
			        	label: "Name:",
			            name: "name"
			        },{
						label: "Description:",
			            name: "description"
			        }
			   ]
			} );
		    editor.on( 'preSubmit', function ( e, o, action ) {
		        if ( action !== 'remove' ) {
		            var name = editor.field( 'name' );
		 
		            if ( ! name.val() ) {
		                    name.error( 'A name must be given' );
		            }
		                 
		            if ( name.val().length >= 255 ) {
		                    name.error( 'The name length must be less that 255 characters' );
		            }
		            // If any error was reported, cancel the submission so it can be corrected
		            if ( this.inError() ) {
		                return false;
		            }
		        }
		    } );			
		    var table = $('#allCT').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
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
				      return '<a href="/ct/'+full.id+'/codeList">'+data+'</a>';
				    }				    
				  },
				  {
				    "targets": 1,
				    "data": null,
				    "defaultContent": ""
				  } 
				],
		        select: {
		            style:    'os',
		            selector: 'td:not(:first-child)'
		        },
		        buttons: [
		            { 
		              extend: "create", 
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                      	window.location='/ct/new';
	                  }
               		},
		            { 
		              extend: "edit", 
		              editor: editor,
		              formButtons: [
                    	'Edit',
                    	{ label: 'Cancel', fn: function () { this.close(); } }
               		  ]
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
			$("#allCT_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
