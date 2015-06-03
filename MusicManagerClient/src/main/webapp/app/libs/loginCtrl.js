/**
 * @author nthienan
 */
// login controller
mainApp.controller('loginCtrl', function($rootScope, $scope, $http, $location, $cookieStore, ngProgress, $translate, langService) {
	$scope.rememberMe = true;
	$scope.haveError = false;
	
	$scope.showSignUp = function() {
		$location.path("/sign-up");
	}
	
	$scope.login = function() {
		ngProgress.start();
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
 					ngProgress.complete();
 					$location.path("/");
 				})
 				.error(function(data, status){
 					ngProgress.complete();
 				});
		})
		.error(function(data, status) {
			$scope.haveError = true;
			$rootScope.error = "Username/password is incorect";
			ngProgress.complete();
		});
	};
	
	$scope.$on('langBroadcast', function() {
		$translate.use(langService.key);
		$scope.lang = langService.key;
    });
});