(function(window, angular, $) {
    'use strict';
    angular.module('CTApp').factory('item', ['$http', '$q', 'ctConfig', function($http, $q, ctConfig) {

        var Item = function(model) {
            this.error = '';
            this.inprocess = false;
            this.hover = false;
            this.model = angular.copy(model);
            this.tempModel = angular.copy(model);
        };

        Item.prototype.hoverIn = function(){
            this.hover = true;
        };

        Item.prototype.hoverOut = function(){
            this.hover = false;
        };

        Item.prototype.update = function() {
            angular.extend(this.model, angular.copy(this.tempModel));
        };

        Item.prototype.revert = function() {
            angular.extend(this.tempModel, angular.copy(this.model));
            this.error = '';
        };

        Item.prototype.deferredHandler = function(data, deferred, defaultMsg) {
            if (!data || typeof data !== 'object') {
                this.error = 'Bridge response error, please check the docs';
            }
            if (data.result && data.result.error) {
                this.error = data.result.error;
            }
            if (!this.error && data.error) {
                this.error = data.error.message;
            }
            if (!this.error && defaultMsg) {
                this.error = defaultMsg;
            }
            if (this.error) {
                return deferred.reject(data);
            }
            this.update();
            return deferred.resolve(data);
        };

        Item.prototype.getEnumerateItemList = function(ctModel) {
            var self = this;
	        if(!this.model.customized){
	            self.inprocess = true;
	            self.error = '';
		        var deferred = $q.defer();
		        var url = "/odm/v1/enumeratedItem?codeListId="+this.model.id;
		        if(ctModel){
		        	url += "&ctId="+ctModel.id;
		        }
	            $http.get(url).success(function(data) {
	                self.deferredHandler(data, deferred);
	            }).error(function(data, status) {
	                self.deferredHandler(data, deferred, 'Error during get enumeratedItemList');
	            })['finally'](function() {
	                self.inprocess = false;
	            });
	            deferred.promise.then(function(data){
	                self.tempModel.enumeratedItemList = self.model.enumeratedItemList = data;
	            });
        	}else{
	            self.inprocess = true;
	            self.error = '';
		        var deferred = $q.defer();
	            $http.get("/odm/v1/customizedEnumeratedItem?codeListId="+this.model.id).success(function(data) {
	                self.deferredHandler(data, deferred);
	            }).error(function(data, status) {
	                self.deferredHandler(data, deferred, 'Error during get enumeratedItemList');
	            })['finally'](function() {
	                self.inprocess = false;
	            });
	            deferred.promise.then(function(data){
	                self.tempModel.enumeratedItemList = self.model.enumeratedItemList = data;
	            });
        	}
        };

        return Item;
    }]);
})(window, angular, jQuery);
