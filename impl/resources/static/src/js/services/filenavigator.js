(function(angular) {
    'use strict';
    angular.module('FileManagerApp').service('fileNavigator', [
        '$http', '$q', 'fileManagerConfig', 'item','$cookies', 'auth', function ($http, $q, fileManagerConfig, Item, $cookies,auth) {
        $http.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        var FileNavigator = function() {
            this.requesting = false;
            this.fileList = [];
            this.currentPath = [];
            this.currentFolderId = [-1];
            this.history = [];
            this.error = '';
        };

        FileNavigator.prototype.deferredHandler = function(data, deferred, defaultMsg) {
            if (!data || typeof data !== 'object') {
                this.error = 'Bridge response error, please check the docs';
            }
            if (!this.error && data.result && data.result.error) {
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
            return deferred.resolve(data);
        };

        FileNavigator.prototype.list = function(onlyFolders) {
            var self = this;
            var deferred = $q.defer();
            var realFolderId = self.currentFolderId.length>1? self.currentFolderId[self.currentFolderId.length-1]:-1;
            var data = {params: {
                onlyFolders: onlyFolders,
                folderId: realFolderId
            }};
	    	var req = {
	    			method : 'POST',
					url : fileManagerConfig.listUrl,
					data : data
				};    	
    		var user = auth.getUser();
    		if(user!=null){
    				req = {
    		    			method : 'POST',
    						url : fileManagerConfig.listUrl,
    						headers : {
    							'Authorization' : user.token_type+" "+user.access_token
    						},
    						data : data
    					};      				
            }

            self.requesting = true;
            self.fileList = [];
            self.error = '';
	    	$http(req).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data, status) {
            	if(status===401){
        	    	auth.setUser(null);
        			$cookies.loginedUser = '';
            		self.deferredHandler({}, deferred, 'Please login First!');
            	}else{
            		self.deferredHandler(data, deferred, 'Error during get folders');
            	}
            })['finally'](function() {
                self.requesting = false;
            });
            return deferred.promise;
        };

        FileNavigator.prototype.refresh = function() {
            var self = this;
            var path = self.currentPath.join('/');
            return self.list(false).then(function(data) {
                	self.fileList = (data.result || []).map(function(file) {
                    return new Item(file, self.currentPath, self.currentFolderId);
                });
                self.buildTree(path);
            });
        };
        
        FileNavigator.prototype.buildTree = function(path) {
            var flatNodes = [], selectedNode = {};

            function recursive(parent, item, path) {
                var absName = path ? (path + '/' + item.model.name) : item.model.name;
                if (parent.name.trim() && path.trim().indexOf(parent.name) !== 0) {
                    parent.nodes = [];
                }
                if (parent.name !== path) {
                    for (var i in parent.nodes) {
                        recursive(parent.nodes[i], item, path);
                    }
                } else {
                    for (var e in parent.nodes) {
                        if (parent.nodes[e].name === absName) {
                            return;
                        }
                    }
                    parent.nodes.push({item: item, name: absName, nodes: []});
                }
            }

            function flatten(node, array) {
                array.push(node);
                for (var n in node.nodes) {
                    flatten(node.nodes[n], array);
                }
            }

            function findNode(data, path) {
                return data.filter(function (n) {
                    return n.name === path;
                })[0];
            }

            !this.history.length && this.history.push({name: '', nodes: []});
            flatten(this.history[0], flatNodes);
            selectedNode = findNode(flatNodes, path);
            if(selectedNode){
            	selectedNode.nodes = [];
            }
            for (var o in this.fileList) {
                var item = this.fileList[o];
                item.isFolder() && recursive(this.history[0], item, path);
            }
        };

        FileNavigator.prototype.folderClick = function(item) {
            this.currentPath = [];
            this.currentFolderId = [-1];
            if (item && item.isFolder()) {
                this.currentPath = item.model.fullPath().split('/').splice(1);
                this.currentFolderId = item.model.fullFolderIds().split(',');
				//console.log(item.model.fullFolderIds());
				//console.log(this.currentFolderId);
            }
            this.refresh();
        };

        FileNavigator.prototype.upDir = function() {
            if (this.currentPath[0]) {
                this.currentPath = this.currentPath.slice(0, -1);
                this.currentFolderId = this.currentFolderId.slice(0, -1);
                this.refresh();
            }
        };

        FileNavigator.prototype.goTo = function(index) {
            this.currentPath = this.currentPath.slice(0, index + 1);
            this.currentFolderId = this.currentFolderId.slice(0, index + 2);
            this.refresh();
        };

        FileNavigator.prototype.fileNameExists = function(fileName) {
            for (var item in this.fileList) {
                item = this.fileList[item];
                if (fileName.trim && item.model.name.trim() === fileName.trim()) {
                    return true;
                }
            }
        };

        FileNavigator.prototype.listHasFolders = function() {
            for (var item in this.fileList) {
                if (this.fileList[item].model.type === 'dir') {
                    return true;
                }
            }
        };

        return FileNavigator;
    }]);
})(angular);