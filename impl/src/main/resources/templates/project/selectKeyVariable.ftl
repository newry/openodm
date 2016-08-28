<@layout>
	<table id="variableList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Order</th>
                <th>CDISC Variable Name</th>
                <th>Description</th>
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
		                	if(value.id){
		                		type="DELETE";
		                	}
		                	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/keyVariable/"+value.sdtmVariable.id,type:type, success: function(result){
		                		location.reload();
    						},
			                error:function(xhr, error, thrown){
			                	errorHandler(editor, xhr, error, thrown, d.action);
	    					}});
		                } );
		            }
        	    },
		        idSrc:  'sdtmVariable.id',
		        table: "#variableList"
		    } );
			editor.on( 'open', function ( e, mode, action ) {
				if(action=='remove'){
                    var data = table.row( { selected: true } ).data();
                    if(data.id){
                    	editor.message("Are you sure you wish to remove "+data.sdtmVariable.name+"?");
                    }else{
                    	editor.message("Are you sure you wish to add "+data.sdtmVariable.name+"?");
                    }
				}
			} );
		    
		    var table = $('#variableList').DataTable( {
		        "aaSorting": [],
		        "paging": false,
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/allKeyVariable",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "orderNumber"},
		            { "data": "sdtmVariable.name" },
		            { "data": "sdtmVariable.description" },
		            { "data": "id", "defaultContent":0}
		        ],
				"columnDefs": [ 
				  {
				    "targets": 3,
				    "data": "id",
				    "render": function ( data, type, full, meta ) {
				      return data > 0 ? "Yes":"No";
				    },
				  }

				],
		        select: {
		            style:    'single'
		        },
		        "createdRow": function (row, data, index ) {
			        $(row).attr('id', data.sdtmVariable.id);
			        if(data.id){
			        	$(row).attr('orderNumber', data.orderNumber);
			        }
        		},
		        buttons: [
		            { 
		              extend: "remove", 
                	  text: "Add/Remove Variable",
 		              editor: editor,
 		              formButtons: [
                    	'Confirm',
                    	{ label: 'Cancel', fn: function () { this.close(); } }
               		  ]
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
		            	$.ajax({url: "/sdtm/v1/project/${prjId?long?c}/domain/${domainId?long?c}/keyVariable/order",contentType:'application/json', type:'POST', data: JSON.stringify(requestData)});
		    		}
		    	}
		    });
		    
		    $("#variableList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
