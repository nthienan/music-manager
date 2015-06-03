/**
 * @author nthienan
 */
//edit user controller
mainApp.controller('editUserCtrl', function($rootScope, $scope, $http, $location, $routeParams, ngProgress, $translate, langService) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get an user by username to edit
	$scope.load = function() {
		ngProgress.start();
		$http.get('/api/user/' + $routeParams.username)
			.success(function(data){
				$scope.editUser = data.response;
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// update
	$scope.updateUser = function() {
		ngProgress.start();
		$http.put('/api/user', $scope.editUser)
			.success(function(data) {
				$location.path('/admin');
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// delete an user
	$scope.deleteOne = function(username){
		ngProgress.start();
		$http.delete('/api/user/' + username)
			.success(function(data){
				$location.path('/admin');
			})
			.error(function(data, status){
				ngProgress.complete();
				alert(data)
			});
	};
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	$scope.$on('langBroadcast', function() {
		$translate.use(langService.key);
		$scope.lang = langService.key;
    });
	
	// load data
	$scope.load();
});