(function(window, angular, $) {
    'use strict';
    angular.module('FileManagerApp').factory('item', ['$http', '$q', '$translate', 'fileManagerConfig', 'chmod', function($http, $q, $translate, fileManagerConfig, Chmod) {

        var Item = function(model, path, folderId) {
            var rawModel = {
                id: model && model.id,
                folderId: folderId || [],
                name: model && model.name || '',
                path: path || [],
                type: model && model.type || 'file',
                size: model && parseInt(model.size || 0),
                date: parseMySQLDate(model && model.date),
                perms: new Chmod(model && model.rights),
                version: model && model.version || '',
                status: model && model.status || '',
                historyList: [],
                recursive: false,
                sizeKb: function() {
                    return Math.ceil(this.size / 1024);
                },
                fullPath: function() {
                    return ('/' + this.path.join('/') + '/' + this.name).replace(/\/\//, '/');
                },
                fullFolderIds: function() {
                    return (this.folderId.join(',') + ',' + this.id).replace(/,,/, ',');
                }
            };

            this.error = '';
            this.inprocess = false;

            this.model = angular.copy(rawModel);
            this.tempModel = angular.copy(rawModel);

            function parseMySQLDate(mysqlDate) {
                var d = (mysqlDate || '').toString().split(/[- :]/);
                return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]);
            }
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

        Item.prototype.createFolder = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'addfolder',
                path: self.tempModel.path.join('/'),
                name: self.tempModel.name
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.createFolderUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
            		self.deferredHandler(data, deferred, $translate.instant('error_creating_folder'));
            	}
            })['finally'](function() {
                self.inprocess = false;
            });
        
            return deferred.promise;
        };

        Item.prototype.rename = function() {
            var self = this;
            var deferred = $q.defer();
            console.log(self.model.fullFolderIds());
            console.log(self.tempModel.fullFolderIds());
            var currentFolderIds =  self.model.fullFolderIds().split(",");
            var newFolderIds =  self.tempModel.fullFolderIds().split(",");
            var data = {params: {
            	id: self.model.id,
                folderId: currentFolderIds.length>2? currentFolderIds[currentFolderIds.length-2]:-1,
                newFolderId: newFolderIds.length>2? newFolderIds[newFolderIds.length-2]:-1,
                name:self.tempModel.name,
                type:self.tempModel.type
            }};
            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.renameUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
                    self.deferredHandler(data, deferred, $translate.instant('error_renaming'));
            	}
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.copy = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'copy',
                path: self.model.fullPath(),
                newPath: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.copyUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
                    self.deferredHandler(data, deferred, $translate.instant('error_copying'));
            	}
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
        /*
        Item.prototype.compress = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'compress',
                path: self.model.fullPath(),
                destination: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.compressUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_compressing'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.extract = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'extract',
                path: self.model.fullPath(),
                sourceFile: self.model.fullPath(),
                destination: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.extractUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_extracting'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
	*/
        Item.prototype.getUrl = function(preview) {
            var path = this.model.fullPath();
            var data = {
                mode: 'download',
                version: this.model.version,
                preview: preview,
                path: path
            };
            return path && [fileManagerConfig.downloadFileUrl, $.param(data)].join('?');
        };

        Item.prototype.download = function(preview) {
            if (this.model.type !== 'dir') {
                window.open(this.getUrl(preview), '_blank', '');
            }
        };

        /*
        Item.prototype.getContent = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'editfile',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.getContentUrl, data).success(function(data) {
                self.tempModel.content = self.model.content = data.result;
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_getting_content'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
		*/
        Item.prototype.getHistory = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'getHistory',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.getHistoryUrl, data).success(function(data) {
                self.tempModel.historyList = self.model.historyList = data.result;
				console.log(self.tempModel.historyList);
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
                    self.deferredHandler(data, deferred, $translate.instant('error_getting_History'));
            	}
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
		

        Item.prototype.remove = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'delete',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.removeUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
            		self.deferredHandler(data, deferred, $translate.instant('error_deleting'));
            	}
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.edit = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'savefile',
                content: self.tempModel.content,
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';

            $http.post(fileManagerConfig.editUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_modifying'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        /*
        Item.prototype.changePermissions = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'changepermissions',
                path: self.tempModel.fullPath(),
                perms: self.tempModel.perms.toOctal(),
                permsCode: self.tempModel.perms.toCode(),
                recursive: self.tempModel.recursive
            }};
            
            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.permissionsUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_changing_perms'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
		*/
        Item.prototype.checkout = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'checkout',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.checkoutUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_checkout'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
		
        Item.prototype.undoCheckout = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'undoCheckout',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.checkoutUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_checkout'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
		

        Item.prototype.checkin = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'checkin',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(fileManagerConfig.checkoutUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, $translate.instant('error_checkin'));
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.isFolder = function() {
            return this.model.type === 'dir';
        };

        Item.prototype.isCheckedOut = function() {
            return this.model.status === 'checkout';
        };
		
        Item.prototype.isEditable = function() {
            return !this.isFolder() && fileManagerConfig.isEditableFilePattern.test(this.model.name);
        };

        Item.prototype.isImage = function() {
            return fileManagerConfig.isImageFilePattern.test(this.model.name);
        };

        Item.prototype.isCompressible = function() {
            return this.isFolder();
        };

        Item.prototype.isExtractable = function() {
            return !this.isFolder() && fileManagerConfig.isExtractableFilePattern.test(this.model.name);
        };

        return Item;
    }]);
})(window, angular, jQuery);
