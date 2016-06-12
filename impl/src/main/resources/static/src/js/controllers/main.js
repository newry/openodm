(function(window, angular, $) {
    'use strict';
    var fileManagerApp = angular.module('CTApp');
    fileManagerApp.controller('CTAPPCtrl', [
        '$scope', '$cookies', 'ctConfig', '$http','fileUploader', '$q', 'item',
      function($scope, $cookies, ctConfig, $http, fileUploader, $q, Item) {
        $scope.config = ctConfig;
        $scope.reverse = false;
        $scope.predicate = ['model.type', 'model.name'];
        $scope.showMDV = false;
        $scope.showCT = false;
        $scope.showProject = false;
        $scope.order = function(predicate) {
            $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
            $scope.predicate[1] = predicate;
        };
        $scope.query = '';
        $scope.codeListQuery = '';
        $scope.requesting = false;
        $scope.modalRequesting = false;
        $scope.modalMDVlist = [];
		$scope.domainTabs =[];
        $scope.modalVersions = [];
        $scope.temp = new Item();
        $scope.tempCT = new Item();
        $scope.tempProject = new Item();
        $scope.tempDomain = new Item();
        $scope.viewTemplate = 'main-table.html';
        $scope.config = ctConfig;
        $scope.fileNavigator = {"history": [$scope.item]};
        $scope.fileUploader = fileUploader;
        $http.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        $scope.originList = [];
        $scope.dataSetJoinTypeList = [{
        	  id: 1,
        	  value: 'sort'
        	}, {
        	  id: 2,
        	  value: 'merge'
        	}, {
        	  id: 3,
        	  value: 'set'
        	}
        ];
        $scope.sortDirectionList = ['asc', 'desc'];
        
        function deferredHandler(data, deferred, defaultMsg) {
        	var error;
            if (!data || typeof data !== 'object') {
            	error = 'Bridge response error';
            }
            if (!error && data.result && data.result.error) {
            	error = data.result.error;
            }
            if (!error && data.error) {
            	error = data.error.message;
            }
            if (!error && defaultMsg) {
                error = defaultMsg;
            }
            if (error) {
                $scope.temp.error = error;
                return deferred.reject(data);
            } 
            return deferred.resolve(data);
        };

	    $scope.getOriginList = function() {
            	$scope.requesting = true;
		        var deferred = $q.defer();
		    	$http.get("/sdtm/v1/origin").success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get metaDataVersion');
	            })['finally'](function() {
	            	$scope.requesting = false;
	            });
		    	deferred.promise.then(function(data){
			    	$scope.originList = (data || []).map(function(file) {
			    		return file;
		            });
		    	})
	    };
        $scope.getOriginList();

	    $scope.getAllMDVs = function(isModal) {
	    	if(!isModal){
		    	if($scope.showMDV){
		    		$scope.showMDV = false;
		    	}else{
		    		$scope.showMDV = true;
		    	}
	    	}
	    	if($scope.showMDV || isModal){
            	$scope.requesting = true;
		        var deferred = $q.defer();
		    	$http.get("/odm/v1/metaDataVersion").success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get metaDataVersion');
	            })['finally'](function() {
	            	$scope.requesting = false;
	            });
		    	deferred.promise.then(function(data){
			    	if(!isModal){
			    		$scope.mdvList = (data || []).map(function(file) {
		                    return new Item(file);
		                });
			    	}else{
			    		$scope.modalMdvList = (data || []).map(function(file) {
		                    return file;
		                });
			    	}
		    	})
	    	}
	    };
	    
	    $scope.getAllCTs = function() {
	    	if($scope.showCT){
	    		$scope.showCT = false;
	    	}else{
	    		$scope.showCT = true;
	    	}
	    	if($scope.showCT){
            	$scope.requesting = true;
		        var deferred = $q.defer();
		    	$http.get("/odm/v1/controlTerminology").success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get controlTerminology');
	            })['finally'](function() {
	            	$scope.requesting = false;
	            });
		    	deferred.promise.then(function(data){
		    		$scope.ctList = (data || []).map(function(file) {
	                    return new Item(file);
	                });
		    	})
	    	}
	        $scope.viewTemplate = 'main-ct-table.html';
	    };

	    $scope.getAllCodeLists = function(id) {
	    	$scope.tempCT = new Item();
        	$scope.requesting = true;
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/codeList?metaDataVersionId="+id).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.itemList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	    };
	    
	    $scope.getAllCodeListsForCT = function(ct) {
	    	$scope.tempCT.tempModel = ct;
        	$scope.requesting = true;
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/codeListForCT?ctId="+ct.id).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.itemList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	        $scope.viewTemplate = 'main-ct-table.html';
	    };

	    $scope.queryCodeListsForCT = function(q) {
        	$scope.modalRequesting = true;
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/codeListQuery?q="+q+"&ctId="+$scope.tempCT.tempModel.id).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.modalRequesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.modalItemList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	    };

	    $scope.addCodeListForCT = function(codeList) {
        	$scope.modalRequesting = true;
	        var deferred = $q.defer();
	    	$http.post("/odm/v1/controlTerminology/"+$scope.tempCT.tempModel.id+"/codeList/"+codeList.id).success(function(data) {
	    		codeList.added=true;
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.modalRequesting = false;
            });
	    	return deferred.promise;
	    };

	    $scope.addMDVtoCT = function(selectedModalMdv) {
	    	if(selectedModalMdv){
	        	$scope.modalRequesting = true;
		        var deferred = $q.defer();
		    	$http.post("/odm/v1/controlTerminology/"+$scope.tempCT.tempModel.id+"/metaDataVersion/"+selectedModalMdv.id).success(function(data) {
	                deferredHandler(data, deferred);
	                $scope.getAllCodeListsForCT($scope.tempCT.tempModel);
		            $scope.modal('addMetadataVersion', true);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get code Lists');
	            })['finally'](function() {
	            	$scope.modalRequesting = false;
	            });
		    	return deferred.promise;
	    	}
	    };

	    $scope.createCT = function() {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	name:$scope.tempCT.tempModel.name,
	        	desc:$scope.tempCT.tempModel.desc
	         };
		     $http.post("/odm/v1/controlTerminology", data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.modal('newCT', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during create CT');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };
	    
	    $scope.updateCT = function() {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	name:$scope.tempCT.tempModel.name,
	        	desc:$scope.tempCT.tempModel.desc
	         };
		     $http.put("/odm/v1/controlTerminology/"+$scope.tempCT.tempModel.id, data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.getAllCodeListsForCT($scope.tempCT.tempModel);
	             $scope.modal('editCT', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during update CT');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };

	    $scope.createCustCodeList = function() {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	ctId:$scope.tempCT.tempModel.id,
	        	name:$scope.tempCustCodeList.tempModel.name,
	        	submissionValue:$scope.tempCustCodeList.tempModel.cdiscsubmissionValue,
	        	description:$scope.tempCustCodeList.tempModel.description
	         };
		     $http.post("/odm/v1/customizedCodeList", data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.getAllCodeListsForCT($scope.tempCT.tempModel);
	             $scope.modal('newCustCodeList', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during create CT');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };

	    $scope.updateCustCodeList = function() {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	name:$scope.tempCustCodeList.tempModel.name,
	        	submissionValue:$scope.tempCustCodeList.tempModel.cdiscsubmissionValue,
	        	description:$scope.tempCustCodeList.tempModel.description
	         };
		     $http.put("/odm/v1/customizedCodeList/"+$scope.tempCustCodeList.tempModel.id, data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.getAllCodeListsForCT($scope.tempCT.tempModel);
	             $scope.modal('editCustCodeList', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during create CT');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };

	    $scope.saveEumeratedItems = function(item) {
	    	// console.log(item);
	    	var eis = item.tempModel.enumeratedItemList;
	    	if(eis && eis.length >0){
	    		var list = eis.map(function(ei) {
	    			if(ei.customized){
		    			if(ei.id){
		    				if(ei.deleted){
		    					if(ei.extended){
				                    return {
				                    	id:ei.id,
				                    	deleted:true,
				                    	extCodeId:ei.extCodeId,
				                    	codedValue:ei.codedValue,
				                    	extended:ei.extended,
				                    	ctId:$scope.tempCT.tempModel.id,
				                    	codeListId:item.tempModel.id
				    		         };
		    					}else{
				                    return {
				                    	id:ei.id,
				                    	deleted:true,
				                    	extCodeId:ei.extCodeId,
				                    	codedValue:ei.codedValue,
				                    	codeListId:item.tempModel.id
				    		         };
		    					}
		    				}else{
		    					if(ei.extended){
				                    return {
				                    	id:ei.id,
				                    	extCodeId:ei.extCodeId,
				                    	codedValue:ei.codedValue,
				                    	extended:ei.extended,
				                    	ctId:$scope.tempCT.tempModel.id,
				                    	codeListId:item.tempModel.id
				    		         };
		    					}else{
				                    return {
				                    	id:ei.id,
				                    	extCodeId:ei.extCodeId,
				                    	codedValue:ei.codedValue,
				                    	codeListId:item.tempModel.id
				    		         };
		    					}
		    				}
		    			}else{
		                    return {
		                    	extCodeId:ei.extCodeId,
		                    	codedValue:ei.codedValue,
		                    	extended: ei.extended,
		                    	ctId:$scope.tempCT.tempModel.id,
		                    	codeListId:item.tempModel.id
		    		         };
		    			}
	    			}
                });
	        	$scope.modalRequesting = true;
			     var deferred = $q.defer();
			     $http.post("/odm/v1/customizedEnumeratedItem", list).success(function(data) {
		             deferredHandler(data, deferred);
		             item.getEnumerateItemList($scope.tempCT.tempModel);
		         }).error(function(data, status) {
		             deferredHandler(data, deferred, 'Error during create CT');
		         })['finally'](function() {
		             $scope.modalRequesting = false;
		         });
	    	}
	    };

        $scope.newCT = function() {
            $scope.tempCT = new Item();
            $scope.temp.error = '';
        };
        
        $scope.newCustCodeList = function() {
            $scope.tempCustCodeList = new Item();
            $scope.temp.error = '';
        };

        $scope.cleanCodeListQuery = function() {
	    	$scope.codeListQuery= '';
            $scope.temp.error = '';
	    }

        $scope.touch = function(item, type) {
            item = item instanceof Item ? item : new Item();
            item.revert();
            item.type = type;
            if($scope.tempProject.tempModel.id){
            	item.model.projectId = $scope.tempProject.tempModel.id;
            }
            $scope.temp = item;
        };

        $scope.touchCustCodeList= function(item) {
            item = item instanceof Item ? item : new Item();
            $scope.tempCustCodeList = item;
            $scope.modal('editCustCodeList');;
        };
        
        $scope.addNewEnumeratedItem = function(list, customized){
        	var newItem = {"new":true, 'customized':true};
        	if(!customized){
        		newItem = {"new":true, 'customized':true, 'extended': true};
        	}
        	list.push(newItem);
        }
        $scope.remove = function(list, item){
        	var index = list.indexOf(item);
        	if (index > -1) {
        		if(item.new){
            		list.splice(index, 1);
        		}else{
        			item.deleted = true;
        		}
        	}
        }
        $scope.removeLibraray = function(list, item){
        	var index = list.indexOf(item);
        	if (index > -1) {
            	list.splice(index, 1);
        	}
        }
        $scope.click = function(item, type) {
        	if(item.type==='domain' || type==='domain'){
        		var existed = false;
        		var index;
        		for(var i=0;i<$scope.domainTabs.length;i++){
        			var domainTab = $scope.domainTabs[i];
        			if(domainTab.title===item.tempModel.sdtmDomain.name){
        				existed = true;
        				domainTab.active=true
        				index = i;
        				break;
        			}
        			
        		}
        		if(!existed){
        			$scope.domainTabs.push({'title': item.tempModel.sdtmDomain.name, 'active':true});
        			index = $scope.domainTabs.length-1;
        			$scope.getAllProjectDomainVariables($scope.tempProject.tempModel, item.tempModel.sdtmDomain);
        		}
        		if(!$scope.tempProject.tempModel.domains){
        			$scope.tempProject.tempModel.domains = [];
        		}
        		$scope.tempProject.tempModel.domains[index] = item;
	            return false;
        	}else{
	            item.getEnumerateItemList($scope.tempCT.tempModel);
	            $scope.modal('enumeratedItemList');
	            return $scope.touch(item);
        	}
        };
	    
        $scope.getEnumeratedItems = function(item) {
	        var deferred = $q.defer();
	        var prj = $scope.tempProject.tempModel;
	    	$http.get("/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/enumeratedItems").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get projects');
            })['finally'](function() {
            	$scope.requesting = false;
            });
            deferred.promise.then(function(data){
            	item.tempModel.enumeratedItemList = item.model.enumeratedItemList = data;
            });
	        $scope.modal('editProjectEnumeratedItemList');
	        return $scope.touch(item);
        };
	    $scope.queryEnumeratedItemsForCodeList = function(item) {
        	$scope.modalRequesting = true;
	        var prj = $scope.tempProject.tempModel;
	        var deferred = $q.defer();
	        var url = "/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/allEnumeratedItemsQuery";
	        if(item.model.enumeratedItemQuery){
	        	url+=("?q="+item.model.enumeratedItemQuery);
	        }
	    	$http.get(url).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.modalRequesting = false;
            });
            deferred.promise.then(function(data){
            	item.tempModel.enumeratedItemList = item.model.enumeratedItemList = data;
            });
	    };
	    $scope.editEnumeratedItem = function(item, ei, remove) {
        	$scope.modalRequesting = true;
	        var prj = $scope.tempProject.tempModel;
	        var deferred = $q.defer();
	        var url = "/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/enumeratedItem/"+ei.id;
	        if(remove){
		    	$http.delete(url).success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get code Lists');
	            })['finally'](function() {
	            	$scope.modalRequesting = false;
	            });
	        }else{
		    	$http.post(url).success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get code Lists');
	            })['finally'](function() {
	            	$scope.modalRequesting = false;
	            });
	        	
	        }
            deferred.promise.then(function(data){
            	$scope.queryEnumeratedItemsForCodeList(item);
            });

	    };
	    
	    $scope.getProjectLibraryList = function(item) {
	        var prj = $scope.tempProject.tempModel;
	        $scope.tempDomainDataSet={};
	        item.model.selectedMainLibrary=null;
	        item.model.selectedJoinType=$scope.dataSetJoinTypeList[0];
	        item.model.selectedLibrary=null;
	        item.model.sql=null;
		    var deferred = $q.defer();
		    $http.get("/sdtm/v1/project/"+prj.id+"/library").success(function(data) {
	            deferredHandler(data, deferred);
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get project libraries');
	        })['finally'](function() {
	        	$scope.requesting = false;
	        });
		    deferred.promise.then(function(data){
		    	prj.libraryList = (data || []).map(function(file) {
	                return new Item(file);
	            });
		    });
	        $scope.modal('newProjectDomainDataSet');
	        return $scope.touch(item);
	    };

	    $scope.generateSQL = function(item) {
	        var prj = $scope.tempProject.tempModel;
	        var sql;
	        if(item.model.selectedJoinType.value=='sort'){
    	        if(item.model.selectedLibrary && item.model.selectedLibrary.selectedDataSet){
    		        var columns = new Array();
    		        var aliasColumns = new Array();
    	        	var dataSet = item.model.selectedLibrary.selectedDataSet;
    	        	var tableName = item.model.selectedLibrary.model.name + "." + dataSet.name;
            		if(dataSet.selectedColumnList && dataSet.selectedColumnList.length > 0){
	    	        	for(var k=0;k<dataSet.selectedColumnList.length;k++){
	    	        		var col = dataSet.selectedColumnList[k];
	    	        		columns.push(col.name);
	    	        		if(col.alias && col.alias!=''){
	    	        			aliasColumns.push(col.name+"="+col.alias);
	    	        		}
	        			}
            		}
    	        	sql = "proc sort data=" + tableName + " out=" + $scope.tempDomainDataSet.name + "(keep=" + columns.join(" ") ;
    	        	if(aliasColumns.length > 0){
    	        		sql += " rename=(" + aliasColumns.join(" ") + ")";
    	        	}
    	        	sql += ");\n";
            		if(dataSet.selectedSortColumnList && dataSet.selectedSortColumnList.length > 0){
        		        var sortColumns = new Array();
	    	        	for(var k=0;k<dataSet.selectedSortColumnList.length;k++){
	    	        		var col = dataSet.selectedSortColumnList[k];
	    	        		if(col.sortDirection && col.sortDirection=='desc'){
		    	        		sortColumns.push("descending "+col.name);
	    	        		}else{
		    	        		sortColumns.push(col.name);
	    	        		}
	        			}
	    	        	sql+=" by "+sortColumns.join(" ") + ";\n";
    	        	}
            		if(dataSet.condition && dataSet.condition!=''){
	    	        	sql+=" where "+dataSet.condition + ";\n";
            		}
            		sql +="run;";
        	        item.model.sql= sql;
    	        }
	        }
//	        if(item.model.selectedMainLibrary && item.model.selectedMainLibrary.selectedMainDataSet && item.model.selectedMainLibrary.selectedMainDataSet.selectedMainColumnList){
//	        	var dataSet = item.model.selectedMainLibrary.selectedMainDataSet;
//	        	var mainTableName = item.model.selectedMainLibrary.model.name + "." + dataSet.name;
//	        	var alias = item.model.selectedMainLibrary.model.name + "_" + dataSet.name;
//        		if(dataSet.selectedMainColumnList && dataSet.selectedMainColumnList.length > 0){
//    	        	for(var k=0;k<dataSet.selectedMainColumnList.length;k++){
//    	        		mainColumns.push(alias + "." + dataSet.selectedMainColumnList[k].name);
//        			}
//        		}
//    	        if(item.model.selectedLibrary && item.model.selectedLibrary.selectedDataSet){
//    	        	var dataSet = item.model.selectedLibrary.selectedDataSet;
//    	        	var alias = item.model.selectedLibrary.model.name + "_" + dataSet.name;
//            		if(dataSet.selectedColumnList && dataSet.selectedColumnList.length > 0){
//        	        	for(var k=0;k<dataSet.selectedColumnList.length;k++){
//        	        		joinColumns.push(alias + "." + dataSet.selectedColumnList[k].name);
//            			}
//            		}
//    	        }
//            	sql = "SELECT "+ mainColumns.join(", ");
//            	if(joinColumns.length > 0){
//            		sql += "." + joinColumns.join(", ")
//            	}
//            	sql += " FROM " + mainTableName + " as " + alias;
//    	        if(item.model.selectedLibrary && item.model.selectedLibrary.selectedDataSet){
//    	        	var dataSet = item.model.selectedLibrary.selectedDataSet;
//    	        	var alias = item.model.selectedLibrary.model.name + "_" + dataSet.name;
//            		sql += " Join " + item.model.selectedLibrary.model.name + "." + dataSet.name + " as " + alias;
//            		if(dateSet.selectedJoinColumnList && dateSet.selectedJoinColumnList.length){
//        	        	for(var k=0;k<dataSet.selectedJoinColumnList.length;k++){
//        	        		joinColumns.push(alias + "." + dataSet.selectedColumnList[k].name);
//            			}
//            		}
//    	        }
//	        }
	    };
	    
        $scope.getCodeList = function(item) {
	        var deferred = $q.defer();
	        var prj = $scope.tempProject.tempModel;
	    	$http.get("/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/codeList").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code list');
            })['finally'](function() {
            	$scope.requesting = false;
            });
            deferred.promise.then(function(data){
            	item.tempModel.codeLists = item.model.codeLists = data;
            });
	        $scope.modal('addCodeListToProject');
	        return $scope.touch(item);
        };
	    $scope.queryCodeListsForProject = function(item) {
        	$scope.modalRequesting = true;
	        var prj = $scope.tempProject.tempModel;
	        var deferred = $q.defer();
	        var url = "/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/codeListQuery";
	        if(item.model.codeListQuery){
	        	url+=("?q="+item.model.codeListQuery);
	        }
	    	$http.get(url).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get code Lists');
            })['finally'](function() {
            	$scope.modalRequesting = false;
            });
            deferred.promise.then(function(data){
            	item.tempModel.codeLists = item.model.codeLists = data;
            });
	    };
	    $scope.addCodeListForProject = function(item, codeList, remove) {
        	$scope.modalRequesting = true;
	        var prj = $scope.tempProject.tempModel;
	        var deferred = $q.defer();
	        var url = "/sdtm/v1/project/"+prj.id+"/variable/"+item.model.id+"/codeList/"+codeList.id;
	        if(remove){
			    $http.delete(url).success(function(data) {
		            deferredHandler(data, deferred);
		        }).error(function(data, status) {
		        	deferredHandler(data, deferred, 'Error during get code Lists');
		        })['finally'](function() {
		        	$scope.modalRequesting = false;
		        });
	            deferred.promise.then(function(data){
	            	item.tempModel.codeList = item.model.codeList = undefined;
	            	$scope.queryCodeListsForProject(item);
	            });
	        }else{
			    $http.post(url).success(function(data) {
		            deferredHandler(data, deferred);
		        }).error(function(data, status) {
		        	deferredHandler(data, deferred, 'Error during get code Lists');
		        })['finally'](function() {
		        	$scope.modalRequesting = false;
		        });
	            deferred.promise.then(function(data){
	            	item.tempModel.codeList = item.model.codeList = codeList;
	            	$scope.queryCodeListsForProject(item);
	            });
	        }

	    };

        $scope.modal = function(id, hide) {
            return $('#' + id).modal(hide ? 'hide' : 'show');
        };

	    $scope.getAllProjects = function() {
	    	if($scope.showProject){
	    		$scope.showProject = false;
	    	}else{
	    		$scope.showProject = true;
	    	}
	    	if($scope.showProject){
            	$scope.requesting = true;
		        var deferred = $q.defer();
		    	$http.get("/sdtm/v1/project").success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get projects');
	            })['finally'](function() {
	            	$scope.requesting = false;
	            });
		    	deferred.promise.then(function(data){
		    		$scope.projectList = (data || []).map(function(file) {
	                    return new Item(file);
	                });
		    	})
	    	}
	    };
        $scope.newProject = function() {
            $scope.tempProject = new Item({});
            $scope.tempProject.tempModel.libraryList=[];
            $scope.temp.error = '';
            $scope.tempCT=new Item({});;
	        var deferred = $q.defer();
	    	$http.get("/sdtm/v1/version").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get versions');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
		    	$scope.modalVersions = (data || []).map(function(file) {
		    		return file;
	            });
	    	})
	    	$scope.modalCts=[];
        };
        
        $scope.getProject = function(id) {
            // $scope.tempProject = new Item({});
            // $scope.tempProject.tempModel.libraryList=[];
            // $scope.temp.error = '';
            // $scope.tempCT=new Item({});;
	        var deferred = $q.defer();
	    	$http.get("/sdtm/v1/project/"+id).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get versions');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.tempProject.tempModel.libraryList = data.libraries;
	    		$scope.tempProject.model = $scope.tempProject.tempModel;
	    	})
        };

	    $scope.createProject = function(selectedModalVersion, selectedModalCt) {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	versionId:selectedModalVersion.id,
	        	ctId:selectedModalCt.id,
	        	name:$scope.tempProject.tempModel.name,
	        	description:$scope.tempProject.tempModel.description
	         };
	         data.libraryList = $scope.tempProject.tempModel.libraryList;
	         // console.log(data);
		     $http.post("/sdtm/v1/project", data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.getAllProjects();
	             $scope.modal('newProject', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during create Project');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };

	    $scope.updateProject = function() {
        	$scope.modalRequesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	name:$scope.tempProject.tempModel.name,
	        	description:$scope.tempProject.tempModel.description
	         };
	         data.libraryList = $scope.tempProject.tempModel.libraryList;
		     $http.put("/sdtm/v1/project/"+$scope.tempProject.tempModel.id, data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.getAllProjects();
	             $scope.modal('editProject', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during update Project');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };

	    $scope.getAllProjectVariables = function(prj) {
	    	$scope.tempProject.tempModel = prj;
	        $scope.varList =[];
        	$scope.requesting = true;
	        var deferred = $q.defer();
	    	$http.get("/sdtm/v1/project/"+prj.id+"/variable").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get all project variables');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.varList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	        $scope.viewTemplate = 'main-project-table.html';
	    };
	    
	    $scope.getProjectDomainVariables = function(prj, domain) {
	        $scope.varList =[];
        	$scope.requesting = true;
        	$scope.tempDomain.tempModel = domain;
	        var deferred = $q.defer();
	    	$http.get("/sdtm/v1/project/"+prj.id+"/domain/"+domain.id+"/variable").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get project domain variables');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.varList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	        $scope.viewTemplate = 'main-project-table.html';
	    };

	    $scope.getAllProjectDomainVariables = function(prj, domain) {
        	$scope.requesting = true;
	        var deferred = $q.defer();
	    	$http.get("/sdtm/v1/project/"+prj.id+"/domain/"+domain.id+"/allVariable").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get project domain variables');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		domain.varOrderMap = [];
	    		domain.variableList = (data || []).map(function(file) {
	    			domain.varOrderMap[file.id] = file.orderNumber;
	    			var item = new Item(file);
	    			item.model.sdtmDomain = item.tempModel.sdtmDomain = domain;
                    return item;
                });
	    	})
	    };
	    
	    $scope.getProjectDomains = function(prj) {
	        $scope.varList =[];
            $scope.requesting = true;
	    	$scope.tempProject.tempModel = prj;
            $scope.tempCT=new Item({});
            $scope.domainTabs=[];
		    var deferred = $q.defer();
		    $http.get("/sdtm/v1/project/"+prj.id+"/domain").success(function(data) {
	            deferredHandler(data, deferred);
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get projects');
	        })['finally'](function() {
	        	$scope.requesting = false;
	        });
		    deferred.promise.then(function(data){
		    	prj.domainOrderMap = [];
		    	prj.domainList = (data || []).map(function(file) {
	                prj.domainOrderMap[file.id] = file.orderNumber;
	                return new Item(file);
	            });
		    });
	        $scope.viewTemplate = 'main-project-table.html';
	    };
        
	    $scope.getKeyVariableList = function(domain) {
		    var deferred = $q.defer();
		    $http.get("/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain/"+domain.model.sdtmDomain.id+"/allKeyVariable").success(function(data) {
	            deferredHandler(data, deferred);
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get projects');
	        })['finally'](function() {
	        	$scope.requesting = false;
	        });
		    deferred.promise.then(function(data){
		    	domain.tempModel.keyVariableOrderMap = [];
		    	domain.tempModel.keyVariableList = (data || []).map(function(file) {
		    		if(file.orderNumber){
		    			domain.tempModel.keyVariableOrderMap[file.sdtmVariable.id] = file.orderNumber;
		    		}
	                return new Item(file.sdtmVariable);
	            });
		    });
	        $scope.viewTemplate = 'main-project-table.html';
	        $scope.touch(domain);
            $scope.modal('editProjectDomainKeyVariable')
	    };

	    $scope.editKeyVariableForProject = function(domain, item, remove) {
        	$scope.modalRequesting = true;
	        var prj = $scope.tempProject.tempModel;
	        var deferred = $q.defer();
	        var url = "/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain/"+domain.model.sdtmDomain.id+"/keyVariable/"+item.model.id;
	        if(remove){
		    	$http.delete(url).success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get code Lists');
	            })['finally'](function() {
	            	$scope.modalRequesting = false;
	            });
	        }else{
		    	$http.post(url).success(function(data) {
	                deferredHandler(data, deferred);
	            }).error(function(data, status) {
	            	deferredHandler(data, deferred, 'Error during get code Lists');
	            })['finally'](function() {
	            	$scope.modalRequesting = false;
	            });
	        	
	        }
            deferred.promise.then(function(data){
            	$scope.getKeyVariableList(domain);
            });

	    };
        $scope.keyVariableSortableOptions = {
            	stop: function(e, ui) {
            		var newChildren = e.target.getElementsByTagName("TR");
            		var requestData = [];
            		for(var i=0; i< newChildren.length; i++){
            			var id = newChildren[i].getAttribute("id");
            			var oldOrder = $scope.temp.tempModel.keyVariableOrderMap[id];
            			var newOrder = i+1;
            			if( oldOrder && oldOrder != newOrder){
    	            		$scope.temp.tempModel.keyVariableOrderMap[id] = newOrder;
    	            		requestData.push({"id":id,"orderNumber":newOrder})
            			}
            		}
        		    if(requestData.length > 0){
                        $scope.requesting = true;
            		    var deferred = $q.defer();
	        	        var url = "/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain/"+$scope.temp.model.sdtmDomain.id+"/keyVariable/order";
	        		    $http.post(url, requestData).success(function(data) {
	        	            deferredHandler(data, deferred);
	        	        }).error(function(data, status) {
	        	            deferredHandler(data, deferred, 'Error during get projects');
	        	        })['finally'](function() {
	        	        	$scope.requesting = false;
	        	        });
            		}
            	}
            };

	    $scope.tocSortableOptions = {
        	stop: function(e, ui) {
        		var newChildren = e.target.getElementsByTagName("TR");
        		var requestData = [];
        		for(var i=1; i< newChildren.length; i++){
        			var id = newChildren[i].getAttribute("id");
        			if($scope.tempProject.tempModel.domainOrderMap[id] != i){
	            		$scope.tempProject.tempModel.domainOrderMap[id] = i;
	            		requestData.push({"id":id,"orderNumber":i})
        			}
        		}
                $scope.requesting = true;
    		    var deferred = $q.defer();
    		    $http.post("/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain", requestData).success(function(data) {
    	            deferredHandler(data, deferred);
    	        }).error(function(data, status) {
    	            deferredHandler(data, deferred, 'Error during get projects');
    	        })['finally'](function() {
    	        	$scope.requesting = false;
    	        });

        	}
        };
        
        $scope.domainSortableOptions = {
            	stop: function(e, ui) {
            		var newChildren = e.target.getElementsByTagName("TR");
            		var requestData = [];
            		var index = newChildren[1].getAttribute("index");
            		var item = $scope.tempProject.tempModel.domains[index];
            		for(var i=1; i< newChildren.length; i++){
            			var id = newChildren[i].getAttribute("id");
            			if(item.tempModel.sdtmDomain.varOrderMap[id] != i){
	                		item.tempModel.sdtmDomain.varOrderMap[id] = i;
	                		requestData.push({"id":id,"orderNumber":i})
            			}
            		}
                    $scope.requesting = true;
        		    var deferred = $q.defer();
        		    $http.post("/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain/"+item.tempModel.sdtmDomain.id+"/variable/order", requestData).success(function(data) {
        	            deferredHandler(data, deferred);
        	        }).error(function(data, status) {
        	            deferredHandler(data, deferred, 'Error during get projects');
        	        })['finally'](function() {
        	        	$scope.requesting = false;
        	        });

            	}
            };


	    $scope.addDomainForProject = function(domain, prj) {
            $scope.requesting = true;
		    var deferred = $q.defer();
		    $http.post("/sdtm/v1/project/"+prj.id+"/domain/"+domain.sdtmDomain.id).success(function(data) {
	            deferredHandler(data, deferred);
	        	domain.status='active';
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get projects');
	        })['finally'](function() {
	        	$scope.requesting = false;
	        });
	    };
	    
	    $scope.removeDomainForProject = function(domain, prj) {
            $scope.requesting = true;
		    var deferred = $q.defer();
		    $http.delete("/sdtm/v1/project/"+prj.id+"/domain/"+domain.sdtmDomain.id).success(function(data) {
	            deferredHandler(data, deferred);
	        	domain.status='inactive';
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get projects');
	        })['finally'](function() {
	        	$scope.requesting = false;
	        });
	    };

	    $scope.getAvailableCTs = function(versionId) {
            $scope.modalRequesting = true;
		    var deferred = $q.defer();
		    $http.get("/sdtm/v1/version/"+versionId+"/ct").success(function(data) {
	            deferredHandler(data, deferred);
	        }).error(function(data, status) {
	            deferredHandler(data, deferred, 'Error during get projects');
	        })['finally'](function() {
	        	$scope.modalRequesting = false;
	        });
	    	deferred.promise.then(function(data){
		    	$scope.modalCts = (data || []).map(function(file) {
		    		return file;
	            });
	    	})

	    };

        $scope.addNewLibraray = function(list){
        	var newItem = {"new":true};
        	list.push(newItem);
        };
        
        
        $scope.closeTab = function(index) {
        	//if(window.confirm("Realy to close?")){
        		$scope.domainTabs.splice(index, 1);
        	//}
        };

        $scope.saveTabData = function(index) {
        	var item = $scope.tempProject.tempModel.domains[index];
        	var variableList = item.tempModel.sdtmDomain.variableList;
        	//console.log(variableList);
    		var requestData = [];
    		var changedVarList = [];
        	if(variableList && variableList.length > 0){
        		for(var i=0; i< variableList.length; i++){
        			var variable = variableList[i];
        			var reqData = {}
        			if(variable.model.changed){
        				changedVarList.push(variable);
        				reqData.id = variable.model.id;
	        			if(variable.model.crfPageNo){
	        				reqData.crfPageNo = variable.model.crfPageNo;
	        			}
	        			if(variable.model.length){
	        				reqData.length = variable.model.length;
	        			}
	        			if(variable.model.origins && variable.model.origins.length>0){
	        				reqData.originList = [];
	        				for(var j=0;j<variable.model.origins.length;j++){
	        					reqData.originList.push(variable.model.origins[j].id);
	        				}
	        			}
                		requestData.push(reqData);
        			}
        		}
        	}
        	if(changedVarList.length > 0){
	            $scope.requesting = true;
			    var deferred = $q.defer();
			    $http.post("/sdtm/v1/project/"+$scope.tempProject.tempModel.id+"/domain/"+item.tempModel.sdtmDomain.id+"/variable", requestData).success(function(data) {
		            deferredHandler(data, deferred);
		        	for(var i=0;i<changedVarList.length;i++){
		        		changedVarList[i].model.changed=false;
		        	}
		        }).error(function(data, status) {
		            deferredHandler(data, deferred, 'Error during updating domain');
		        })['finally'](function() {
		        	$scope.requesting = false;
		        });
        	}
        	
        	//console.log(requestData);
        };

        $scope.editOrigin = function(id, show) {
        	if(show){
	        	$('#originListSelect_' + id).show();
	        	$('#originList_' + id).hide();
        	}else{
	        	$('#originListSelect_' + id).hide();
	        	$('#originList_' + id).show();
        	}
        };
        
        $scope.markAsChanged = function(item){
        	item.changed=true;
        }

    }]);
})(window, angular, jQuery);
