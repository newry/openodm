<@layout>
	<table id="allVar" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Order</th>
                <th>CDISC Variable Name</th>
                <th>Controlled Terminology</th>
                <th>Origin</th>
                <th>CRF Page No.</th>
                <th>Role</th>
                <th>Core</th>
                <th>Type</th>
                <th>Length</th>
            </tr>
        </thead>
	</table>
	<script>
		(function ($, DataTable) {
			if ( ! DataTable.ext.editorFields ) {
			    DataTable.ext.editorFields = {};
			}
			 
			var Editor = DataTable.Editor;
			var _fieldTypes = DataTable.ext.editorFields;
			_fieldTypes.todo = {
			    create: function ( conf ) {
			        var that = this;
			        conf._enabled = true;
		            $.ajax({url: "/sdtm/v1/origin", async: false, success: function(result){
		            	var html = '<select id="'+Editor.safeId( conf.id )+'" multiple size="'+result.length+'">';
		            	result.map(function(row){
		            		html += ('<option value="'+row.id+'">'+row.name+'</option>');
		            	});
				        html += '</select>';
				        conf._input = $(html);
    				}});
			        return conf._input;
			    },
			 
			    get: function ( conf ) {
			    	var result =[];
			        $.each($('option:selected', conf._input), function(){            
			            result.push(parseInt($(this).val()));
			        });
			    	return result;
			    },
			 
			    set: function ( conf, val ) {
			        $.each($('option', conf._input), function(){            
			    		$(this).prop('selected', false);
			        });
			    	if(val){
			    		val.map(function(row){
			    			var op = $('option[value="'+row.id+'"]', conf._input);
			    			op.prop('selected', true);
			    		});
			    	}
			    },
			 
			    enable: function ( conf ) {
			        conf._enabled = true;
			        $(conf._input).removeClass( 'disabled' );
			    },
			 
			    disable: function ( conf ) {
			        conf._enabled = false;
			        $(conf._input).addClass( 'disabled' );
			    }
			};
	 	})(jQuery, jQuery.fn.dataTable);
		$(document).ready(function() {
			editor = new $.fn.dataTable.Editor( {
				ajax: function ( method, url, d, successCallback, errorCallback ) {
		            if ( d.action === 'edit' ) {
		                $.each( d.data, function (id, value) {
		                	var type="POST";
		                	var data={};
		                	data.id = parseInt(id);
		                	if(value.length){
		                		data.length = value.length;
		                	}
		                	if(value.crfPageNo){
		                		data.crfPageNo = value.crfPageNo;
		                	}
		                	if(value.origins){
		                		data.originList = value.origins;
		                	}
		                	var array = new Array();
		                	array.push(data);
		                	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/variable",contentType:'application/json', type:'POST', data: JSON.stringify(array), success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
        	   },
        	   idSrc:  'id',
			   table: "#allVar",
			   fields: [ 
			   		{
			        	label: "Origin:",
			            name: "origins",
			            type: "todo"
			        },{
						label: "CRF Page No.:",
			            name: "crfPageNo"
			        },{
						label: "Length:",
			            name: "length"
			        }
			   ]
			} );
		    editor.on( 'preSubmit', function ( e, o, action ) {
		        if ( action !== 'remove' ) {
		            var origins = editor.field( 'origins' );
		            var crfPageNo = editor.field( 'crfPageNo' );
		            if (origins.val() && contains(origins.val(), 2)) {
		            	if(!crfPageNo.val() || crfPageNo.val()==''){
		                	crfPageNo.error( 'A CRF Page No. must be given' );
		                }
		            }
		                 
		            // If any error was reported, cancel the submission so it can be corrected
		            if ( this.inError() ) {
		                return false;
		            }
		        }
		    } );
		    var table = $('#allVar').DataTable( {
		        "aaSorting": [],
		        "paging": false,
		        "bFilter": false,
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/allVariable",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "orderNumber" },
		            { "data": "sdtmVariable.name" },
		            { "data": "codeList", "defaultContent": {} },
		            { "data": "origins", "defaultContent": []},
		            { "data": "crfPageNo", "defaultContent": "" },
		            { "data": "role" },
		            { "data": "core" },
		            { "data": "sdtmVariable.sasDataType" },
		            { "data": "length", "defaultContent": ""  }
		        ],
				"columnDefs": [ 
				  {
				    "targets": 2,
				    "data": "sdtmVariable.codeList",
				    "render": function ( data, type, full, meta ) {
				      var result ="";
				      if(data){
				      	result = data.cdiscsubmissionValue;
				      }
				      return result;
				    },
				  },
				  {
				    "targets": 3,
				    "data": "origins",
				    "render": function ( data, type, full, meta ) {
				      var result ="";
				      if(data){
					      data.map(function(row, index){
					      	if(index==0){
					      		result += row.name;
					      	}else{
					      		result += "<br/>" + row.name;
					      	}
					      });
				      }
				      return result;
				    },
				  }
				],
		        select: {
		            style:    'single',
		            selector: 'td:not(:nth-child(1))'
		        },
		        "createdRow": function (row, data, index ) {
			        $(row).attr('id', data.id);
			        $(row).attr('orderNumber', data.orderNumber);
        		},
		        buttons: [
		            { 
		              extend: "edit", 
		              editor: editor,
		              formButtons: [
                    	'Confirm',
                    	{ label: 'Cancel', fn: function () { this.close(); } }
               		  ]
              		},
               		{
               		  extend: "edit", 
		              text: 'Select Control Terminology',
		              editor: editor,
                      action: function ( e, dt, node, config ) {
                      	var data = table.row( { selected: true } ).data();
                      	window.location='/project/${prjId?long?c}/domain/${domainId?long?c}/variable/'+data.id+'/selectCodeList';
	                  }
               		
               		}
		        ]
		    });
		    table.rowReordering(
		    {
		    	fnUpdateCallback: function(sRequestData){
		    		var trs = $("tr[orderNumber]");
        			var requestData = [];
		    		trs.map(function(index ,row){
		    			var oldOrder = parseInt(row.getAttribute("orderNumber"));
		    			if((index+1)!= oldOrder){
		    				requestData.push({"id":row.id,"orderNumber":index+1});
		    			}
		    		});
		    		requestData.map(function(data){
		    			$("#"+data.id).attr("orderNumber", data.orderNumber);
		    		});
		    		if(requestData.length > 0){
		            	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/variable/order",contentType:'application/json', type:'POST', data: JSON.stringify(requestData)});
		    		}
		    	}
		    });
			$("#allVar_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
