<@layout>
	<table id="dataSetList" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Action</th>
                <th>Output Dataset</th>
                <th>Input Dataset(s)</th>
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
				"columnDefs": [ 
				  {
				    "targets": 0,
				    "data": "joinType",
				  },
				  {
				    "targets": 1,
				    "render": function ( data, type, full, meta ) {
				      return full.sdtmProjectLibrary.name+"."+full.name;
				    },
				  },
				  {
				    "targets": 2,
				    "render": function ( data, type, full, meta ) {
				      var obj = jQuery.parseJSON(full.metaData);
				      if(full.joinType=='sort'){
				      	return obj.libraryName+"."+obj.dataSet.substring(0,obj.dataSet.indexOf("."));
				      }else{
				      	return "";
				      }
				    }				    
				  }
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
