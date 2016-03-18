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

	    $scope.getAllMDVs = function() {
	    	if($scope.showMDV){
	    		$scope.showMDV = false;
	    	}else{
	    		$scope.showMDV = true;
	    	}
	    	if($scope.showMDV){
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
		    		$scope.mdvList = (data || []).map(function(file) {
	                    return new Item(file);
	                });
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
	    	deferred.promise.then(function(data){
	    		$scope.modalItemList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
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
	        	id:$scope.tempCT.tempModel.id,
	        	name:$scope.tempCT.tempModel.name,
	        	desc:$scope.tempCT.tempModel.desc
	         };
		     $http.put("/odm/v1/controlTerminology", data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.modal('editCT', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during update CT');
	         })['finally'](function() {
	             $scope.modalRequesting = false;
	         });
	    };


        $scope.newCT = function() {
            $scope.tempCT = new Item();
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
