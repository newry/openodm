<@layout>
	<table id="allTOC" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Order</th>
                <th>Domain</th>
                <th>Label</th>
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
		 
		            if ( d.action === 'remove' ) {
		                $.each( d.data, function (id, value) {
		                	var type="POST";
		                	if(value.status=='active'){
		                		type="DELETE";
		                	}
		                	$.ajax({url: "/sdtm/v1/project/${prjId}/domain/"+value.sdtmDomain.id,type:type, success: function(result){
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
			   table: "#allTOC"
			} );
			editor.on( 'open', function ( e, mode, action ) {
				if(action=='remove'){
                    var data = table.row( { selected: true } ).data();
                    if(data.status=='active'){
                    	editor.message("Are you sure you wish to deactivate "+data.sdtmDomain.name+"?");
                    }else{
                    	editor.message("Are you sure you wish to activate "+data.sdtmDomain.name+"?");
                    }
				}
			} );
			
		    var table = $('#allTOC').DataTable( {
		        "aaSorting": [],
		        "paging": false,
		        "bFilter": false,
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/sdtm/v1/project/${prjId}/domain",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "orderNumber" },
		            { "data": "sdtmDomain.name" },
		            { "data": "sdtmDomain.description", "defaultContent": ""},
		            { "data": "creator" },
		            { "data": "updatedBy" },
		            { "data": "dateAdded" },
		            { "data": "dateLastModified" }
		        ],
				"columnDefs": [ 
				  {
				    "targets": 1,
				    "render": function ( data, type, full, meta ) {
				      return '<a href="/project/'+full.id+'/toc">'+data+'</a>';
				    }				    
				  }
				],
		        select: {
		            style:    'os',
		            selector: 'td:not(:nth-child(1))'
		        },
		        "createdRow": function (row, data, index ) {
		        	if(data.status!='active'){
                		$('td', row).addClass('strike');
                	}
			        $(row).attr('id', data.id);
			        $(row).attr('orderNumber', data.orderNumber);
        		},
		        buttons: [
		            { 
		              extend: "remove", 
		              text: 'Activate/Deactivate',
		              
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
		    		if(requestData.length > 0){
		            	$.ajax({url: "/sdtm/v1/project/${prjId}/domain",contentType:'application/json', type:'POST', data: JSON.stringify(requestData)});
		    		}
		    	}
		    });
			$("#allTOC_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
