<@layout>
	<table id="codeList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Submission Value</th>
                <th>Code Id</th>
                <th>Extensible</th>
            </tr>
        </thead>
	</table>
	<script>
		$(document).ready(function() {
		    editor = new $.fn.dataTable.Editor( {
		        idSrc:  'id',
		        table: "#codeList"
		    } );
		    
		    var table = $('#codeList').DataTable( {
		        "aaSorting": [],
		        "dom": "Bfrtip",
		    	"bLengthChange": false,
		        "ajax": {
		        	"url":"/odm/v1/codeListQuery?ctId=${ctId}&q=",
		        	"dataSrc": ""
		        },
		        "columns": [
		            { "data": "name" },
		            { "data": "cdiscsubmissionValue" },
		            { "data": "extCodeId" },
		            { "data": "codeListExtensible" },
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
				  }

				],
				select: true,
		        buttons: [
		            { 
		              extend: "selected", 
                	  text: "Add Code List",
                      action: function ( e, dt, node, config ) {
                      	var rows = table.rows( { selected: true } );
                      	var datas = table.rows( { selected: true } ).data();
                      	var value = {};
                      	value.codeListIds = [];
                      	datas.map(function(obj){
                      		value.codeListIds.push(obj.id);
                      	});
		                $.ajax({url: "/odm/v1/controlTerminology/${ctId}/codeList/",contentType:'application/json', data: JSON.stringify(value), type:'POST', success: function(result){
		                	rows.remove().draw(false);
    					}});
	                  }
               		}
		        ]
		    });
		    $("#codeList_wrapper").css("width", "100%");
		} );
	</script>
</@layout>>
