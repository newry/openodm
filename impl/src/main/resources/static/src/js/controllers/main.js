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
        $scope.requesting = false;
        $scope.temp = new Item();
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
	    
	    $scope.getAllCodeListsForCT = function(id) {
        	$scope.requesting = true;
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/codeListForCT?ctId="+id).success(function(data) {
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

	    
	    $scope.createCT = function() {
        	$scope.requesting = true;
		     var deferred = $q.defer();
	         var data = {
	        	name:$scope.temp.tempModel.name,
	        	desc:$scope.temp.tempModel.desc
	         };
		     $http.post("/odm/v1/controlTerminology", data).success(function(data) {
	             deferredHandler(data, deferred);
	             $scope.modal('newCT', true)
	         }).error(function(data, status) {
	             deferredHandler(data, deferred, 'Error during get controlTerminology');
	         })['finally'](function() {
	             $scope.requesting = false;
	         });
	    };

        $scope.newItem = function() {
            $scope.temp = new Item();
        };

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
