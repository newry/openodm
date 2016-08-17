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
				 ajax: function ( method, url, d, successCallback, errorCallback ) {
		            if ( d.action === 'create' ) {
		                $.each( d.data, function (key, value) {
		                	value.ctId = ${ctId};
		                	$.ajax({url: "/odm/v1/controlTerminology/${ctId}/customizedCodeList",contentType:'application/json', type:'POST', data: JSON.stringify(value), success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
		            else if ( d.action === 'edit' ) {
					    var data = table.row( { selected: true } ).data();
		            	if(data.customized){
			                $.each( d.data, function (id, value) {
			                	$.ajax({url: "/odm/v1/customizedCodeList/"+id,contentType:'application/json', type:'PUT', data: JSON.stringify(value), success: function(result){
			                		location.reload();
	    						},
			                	error:function(xhr, error, thrown){
			                		errorHandler(editor, xhr, error, thrown, d.action);
	    						}
	    						});
			                });
		                }else{
			               	location.reload();
		                }
		            }
		            else if ( d.action === 'remove' ) {
					    var data = table.row( { selected: true } ).data();
		            	if(data.customized){
			                $.each( d.data, function (id, value) {
			                	$.ajax({url: "/odm/v1/controlTerminology/${ctId}/customizedCodeList/"+id,type:'DELETE', success: function(result){
			                		location.reload();
	    						},
			                	error:function(xhr, error, thrown){
			                		errorHandler(editor, xhr, error, thrown, d.action);
	    						}
	    						});
			                } );
		                }else{
			                $.each( d.data, function (id, value) {
			                	$.ajax({url: "/odm/v1/controlTerminology/${ctId}/codeList/"+id,type:'DELETE', success: function(result){
			                		location.reload();
	    						},
			                	error:function(xhr, error, thrown){
			                		errorHandler(editor, xhr, error, thrown, d.action);
	    						}
	    						});
			                } );
		                }
		            }
        	    },
		        idSrc:  'id',
		        table: "#codeList",
		        fields: [
		        	{
		                label: "Name:",
		                name: "name"
		            },
		        	{
		                label: "Description:",
		                name: "description"
		            },
		        	{
		                label: "Submission Value:",
		                name: "cdiscsubmissionValue"
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
		    
		    var table = $('#codeList').DataTable( {
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
		            { "data": "cdiscsubmissionValue" ,"defaultContent": ""},
		            { "data": "extCodeId","defaultContent": "" },
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
				    "targets": 1,
				    "data": "description",
				    "createdCell": function (td, cellData, rowData, row, col) {
					      //if ( rowData.customized ) {
					        $(td).addClass('selectable')
					      //}
				    }
				  },
				  {
				    "targets": 2,
				    "data": "cdiscsubmissionValue",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    $(td).addClass('selectable')
				    }
				  },
				  {
				    "targets": 3,
				    "data": "extCodeId",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    $(td).addClass('selectable')
				    }
				  },
				  {
				    "targets": 4,
				    "data": "codeListExtensible",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    $(td).addClass('selectable')
				    }
				  },
				  {
				    "targets": 5,
				    "data": "customized",
				    "render": function ( data, type, full, meta ) {
				      return data?"Yes":"No";
				    },
				    "createdCell": function (td, cellData, rowData, row, col) {
					    $(td).addClass('selectable')
				    }
				  }
				],
				select: {
					style:    'single',
					selector: '.selectable'
				},
		        buttons: [
		            { extend: "create", editor: editor,
		         		formButtons: [
	                    	'Edit',
	                    	{ label: 'Cancel', fn: function () { this.close(); } }
	               		]
		            },
		            { 
		              extend: "create", 
                	  text: "Select Code List",
                      action: function ( e, dt, node, config ) {
                      	window.location='/ct/${ctId}/selectCodeList';
	                  }
               		},
		            { extend: "edit",   editor: editor,
						formButtons: [
	                    	'Edit',
	                    	{ label: 'Cancel', fn: function () { this.close(); } }
	               		]
		            },
		            { extend: "remove", editor: editor, 
		         		formButtons: [
	                    	'Edit',
	                    	{ label: 'Cancel', fn: function () { this.close(); } }
	               		]
		            }
		        ]
		    });
		    $("#codeList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
