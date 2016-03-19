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
        $scope.order = function(predicate) {
            $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
            $scope.predicate[1] = predicate;
        };
        $scope.query = '';
        $scope.codeListQuery = '';
        $scope.requesting = false;
        $scope.modalRequesting = false;
        $scope.modalMDVlist = [];
        $scope.temp = new Item();
        $scope.tempCT = new Item();
        $scope.viewTemplate = 'main-table.html';
        $scope.config = ctConfig;
        $scope.fileNavigator = {"history": [$scope.item]};
        $scope.fileUploader = fileUploader;
        $http.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        
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
			    		if($scope.modalMdvList.length > 0){
//			    			$scope.selectedModalMdv = $scope.modalMdvList[0];
			    		}
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

        $scope.touch = function(item) {
            item = item instanceof Item ? item : new Item();
            item.revert();
            $scope.temp = item;
        };

        $scope.touchCustCodeList= function(item) {
            item = item instanceof Item ? item : new Item();
            $scope.tempCustCodeList = item;
            $scope.modal('editCustCodeList');;
        };
        
        $scope.addNewEnumeratedItem = function(list){
        	var newItem = {"new":true};
        	list.push(newItem);
        }
        $scope.remove = function(list, item){
        	var index = list.indexOf(item);
        	if (index > -1) {
        		list.splice(index, 1);
        	}
        }
        $scope.click = function(item) {
            item.getEnumerateItemList();
            $scope.modal('enumeratedItemList');
            return $scope.touch(item);
        };
	    
        $scope.modal = function(id, hide) {
            return $('#' + id).modal(hide ? 'hide' : 'show');
        };

    }]);
})(window, angular, jQuery);
