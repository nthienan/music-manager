/**
 * @author nthienan
 */
// active controller
mainApp.controller('activeCtrl', function($scope, $rootScope, $http, $location, $routeParams, ngProgress) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	$scope.active = function() {
		ngProgress.start();
		$http.post('/api/user/active?username=' + $routeParams.username + '&activeToken=' + $routeParams.token)
			.success(function(data, status){
				$rootScope.activeStatus = true;
				ngProgress.complete();
				$location.path('/login');
			})
			.error(function(data, status){
				$scope.haveError = true;
				$rootScope.activeStatus = false;
				$rootScope.error = "Have error while activate account. Please try again!";
				ngProgress.complete();
			});
	};
	
	$scope.active();
});