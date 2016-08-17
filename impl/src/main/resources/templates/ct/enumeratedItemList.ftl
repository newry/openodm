<@layout>
	<table id="eiList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Code</th>
                <th>CDISC Submission Value</th>
                <th>CDISC Synonyms</th>
                <th>CDISC Definition</th>
                <th>NCI Preferred Term</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
			<#if customized || extended>
		          <#if customized>
		          		var serviceUrl = "/odm/v1/controlTerminology/${ctId}/customizedCodeList/${codeListId}/customizedEnumeratedItem/";
		          <#else>
		          		var serviceUrl = "/odm/v1/controlTerminology/${ctId}/codeList/${codeListId}/extendedEnumeratedItem/";
		          </#if>
		    editor = new $.fn.dataTable.Editor( {
				 ajax: function ( method, url, d, successCallback, errorCallback ) {
		            if ( d.action === 'create' ) {
		                $.each( d.data, function (key, value) {
		                	$.ajax(
		                		{
		                			url: serviceUrl,
		                			contentType:'application/json',
            		                type:'POST',
		                			data: JSON.stringify(value),
		                			success: function(result){
		                				location.reload();
    								},
		                			error:function(xhr, error, thrown){
		                				errorHandler(editor, xhr, error, thrown, d.action);
    								}
    							}
    						);
		                } );
		            }
		            else if ( d.action === 'edit' ) {
			            $.each( d.data, function (id, value) {
			            	$.ajax({url: serviceUrl+id, contentType:'application/json', type:'PUT', data: JSON.stringify(value), 
			            	success: function(result){
		                		location.reload();
	    					},
		                	error:function(xhr, error, thrown){
		                		serrorHandler(editor, xhr, error, thrown, d.action);
    						}
	    					});
			            });
		            }
		            else if ( d.action === 'remove' ) {
			            $.each( d.data, function (id, value) {
			            	$.ajax({url: serviceUrl+id, type:'DELETE', 
			            	success: function(result){
		                		location.reload();
	    					},
		                	error:function(xhr, error, thrown){
		                		serrorHandler(editor, xhr, error, thrown, d.action);
    						}
	    					});
			            } );
		            }
        	    },
		        idSrc:  'id',
		        table: "#eiList",
		        fields: [
		        	{
		                label: "Code:",
		                name: "extCodeId"
		            },
		        	{
		                label: "CDISC Submission Value:",
		                name: "codedValue"
		            }	        
		        ]
		    } );
		    editor.on( 'preSubmit', function ( e, o, action ) {
		        if ( action !== 'remove' ) {
		            var name = editor.field( 'extCodeId' );
		 
		            if ( ! name.val() ) {
		                    name.error( 'A Code must be given' );
		            }
		                 
		            var codedValue = editor.field( 'codedValue' );
		 
		            if ( ! codedValue.val() ) {
		                    codedValue.error( 'A CDISC Submission Value must be given' );
		            }

		            // If any error was reported, cancel the submission so it can be corrected
		            if ( this.inError() ) {
		                return false;
		            }
		        }
		    } );			
		    </#if>
		    var table = $('#eiList').DataTable( {
		        "aaSorting": [],
				<#if customized || extended>
		        "dom": "Bfrtip",
		        </#if>
		    	"bLengthChange": false,
		        "ajax": {
		        	"url": <#if customized>"/odm/v1/customizedEnumeratedItem?ctId=${ctId}&codeListId=${codeListId}"<#else>"/odm/v1/enumeratedItem?ctId=${ctId}&codeListId=${codeListId}"</#if>,
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "extCodeId" },
		            { "data": "codedValue" },
		            { "data": "cdiscsynonym","defaultContent": ""},
		            { "data": "cdiscdefinition","defaultContent": ""},
		            { "data": "preferredTerm","defaultContent": ""}
		        ],
				"columnDefs": [ 
				  {
				    "targets": 0,
				    "data": "extCodeId",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    if (rowData.customized ) {
					    	$(td).addClass('selectable')
						}
				    }
				  },
				  {
				    "targets": 1,
				    "data": "codedValue",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    if (rowData.customized ) {
					    	$(td).addClass('selectable')
						}
				    }
				  },
				  {
				    "targets": 2,
				    "data": "cdiscsynonym",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    if (rowData.customized ) {
					    	$(td).addClass('selectable')
						}
				    }
				  },
				  {
				    "targets": 3,
				    "data": "cdiscdefinition",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    if (rowData.customized ) {
					    	$(td).addClass('selectable')
						}
				    }
				  },
				  {
				    "targets": 4,
				    "data": "preferredTerm",
				    "createdCell": function (td, cellData, rowData, row, col) {
					    if (rowData.customized ) {
					    	$(td).addClass('selectable')
						}
				    }
				  }
				]
				<#if customized || extended>
					,
					select: {
						style:    'single',
						selector: '.selectable'
					},
			        buttons: [
			            { 
			              extend: "create", 
			              editor: editor,
			              formButtons: [
	                    	'Create',
	                    	{ label: 'Cancel', fn: function () { this.close(); } }
	               		  ]
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
			    </#if>
		    });
		    $("#eiList_wrapper").css("width", "100%");
		} );	
	</script>
</@layout>>
