(function(window, angular, $) {
    'use strict';
    var fileManagerApp = angular.module('CTApp');
    fileManagerApp.controller('CTAPPCtrl', [
        '$scope', '$cookies', 'ctConfig', '$http','fileUploader', '$q', 'item',
      function($scope, $cookies, ctConfig, $http, fileUploader, $q, Item) {
        $scope.config = ctConfig;
        $scope.reverse = false;
        $scope.predicate = ['model.type', 'model.name'];        
        $scope.order = function(predicate) {
            $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
            $scope.predicate[1] = predicate;
        };

        $scope.query = '';
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
                return deferred.reject(data);
            } 
            return deferred.resolve(data);
        };

	    $scope.getAllMDVs = function() {
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/metaDataVersion").success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get folders');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.mdvList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
	    };
	    
	    $scope.getAllCodeLists = function(id) {
	        var deferred = $q.defer();
	    	$http.get("/odm/v1/codeList?metaDataVersionId="+id).success(function(data) {
                deferredHandler(data, deferred);
            }).error(function(data, status) {
            	deferredHandler(data, deferred, 'Error during get folders');
            })['finally'](function() {
            	$scope.requesting = false;
            });
	    	deferred.promise.then(function(data){
	    		$scope.itemList = (data || []).map(function(file) {
                    return new Item(file);
                });
	    	})
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
