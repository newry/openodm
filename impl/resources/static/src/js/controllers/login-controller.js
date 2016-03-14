(function(angular, $) {
	'use strict';
//	angular.module('FileManagerApp').controller(
//			'LoginController',
//			[ '$scope', '$location', '$window', 'Auth', '$routeParams',
//					function($scope, $location, $window, Auth, $routeParams) {
//					     $scope.name = "LoginController";
//					     $scope.params = $routeParams;
//					     $scope.loginUser = function() {
//							var user = {
//								'name' : 'admin'
//							}
//							Auth.setUser(user);
//						}
//					} ]);
//
	angular.module('FileManagerApp').factory("Auth", function() {
		var user;

		return {
			getUser : function() {
				return user;
			},
			setUser : function(aUser) {
				user = aUser;
			},
			isLoggedIn : function() {
				return (user) ? user : false;
			}
		}
	});

})(angular, jQuery);