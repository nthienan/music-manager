/**
 * @author nthienan
 */
//edit user controller
mainApp.controller('editUserCtrl', function($rootScope, $scope, $http, $location, $routeParams) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get an user by username to edit
	$scope.load = function() {
		$http.get('/api/user/' + $routeParams.username)
			.success(function(data){
				$scope.editUser = data.response;
		});
	};
	
	// update
	$scope.updateUser = function() {
		$http.put('/api/user', $scope.editUser)
			.success(function(data) {
				$location.path('/admin');
		});
	};
	
	// delete an user
	$scope.deleteOne = function(username){
		$http.delete('/api/user/' + username)
			.success(function(data){
				$location.path('/admin');
			})
			.error(function(data, status){
				alert(data)
			});
	};
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	// load data
	$scope.load();
});