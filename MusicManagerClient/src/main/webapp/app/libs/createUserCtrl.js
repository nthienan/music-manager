/**
 * @author nthienan
 */
// create user controller
mainApp.controller('createUserCtrl', function($scope, $http, $location){
	$scope.selectedRole = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	// create new user
	$scope.createUser = function(){
		$scope.regisUser.password = $scope.password;
		$scope.regisUser.roles = $scope.selectedRole;
		$http.post('/api/user/regis', $scope.regisUser)
			.success(function(data){
				$location.path("/admin");
			})
			.error(function(data){
				$scope.error = "Some error while add user. Please try again!";
				$scope.haveError = true;
			});
	};
	
	// push or splice selected role
	$scope.selectRole = function(role){
		var idx = $scope.selectedRole.indexOf(role);
		if(idx > -1)
			$scope.selectedRole.splice(idx, 1);
		else
			$scope.selectedRole.push(role);
	};
});