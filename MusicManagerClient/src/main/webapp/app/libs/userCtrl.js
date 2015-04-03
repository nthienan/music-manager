/**
 * @author nthienan
 */
//user controller
mainApp.controller('userCtrl', function($rootScope, $scope, $http, $location) {
	$scope.haveError = false;
	
	// create new user
	$scope.createUser = function(){
		$scope.regisUser.password = $scope.password;
		$http.post('/api/user/regis', $scope.regisUser)
			.success(function(data){
				$rootScope.regisSuccess = true;
				$location.path("/login");
			})
			.error(function(data){
				$scope.error = "Some error while regis user. Please try again!";
				$scope.haveError = true;
			});
	};
	
	// get an user
	$scope.getUser = function() {
		$http.get('/api/user/' + $rootScope.user.username)
			.success(function(data){
				$scope.account = data.response;
			})
			.error(function(data){
				$location.path('/');
			});
	}
});