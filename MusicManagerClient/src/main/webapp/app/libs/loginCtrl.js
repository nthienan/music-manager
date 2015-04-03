/**
 * @author nthienan
 */
// login controller
mainApp.controller('loginCtrl', function($rootScope, $scope, $http, $location, $cookieStore) {
	$scope.rememberMe = true;
	$scope.haveError = false;
	
	$scope.showSignUp = function() {
		$location.path("/sign-up");
	}
	
	$scope.login = function() {
		$http({
			method: 'POST',
			url: '/api/user/authenticate/' + $scope.username + '/' + $scope.password,
			headers : {'Content-Type': 'application/x-www-form-urlencoded'},
		})
		.success(function(data, status) {   
			var authToken = data.token;
			$scope.haveError = false;
			$rootScope.authToken = authToken;
			
			$cookieStore.put('authToken', authToken);
			$http.get('/api/user/get')
 				.success(function(data){
 					$rootScope.user = data.response;
 					$rootScope.authenticated = true;
 					$location.path("/");
 				});
		})
		.error(function(data, status) {
			$scope.haveError = true;
			$rootScope.error = "Username/password is incorect";
		});
	};
});