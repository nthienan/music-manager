/**
 * @author nthienan
 */
//user controller
mainApp.controller('userCtrl', function($rootScope, $scope, $http, $location, ngProgress, $translate, langService) {
	$scope.haveError = false;
	
	// create new user
	$scope.createUser = function(){
		ngProgress.start();
		$scope.regisUser.password = $scope.password;
		var formData = new FormData();
		formData.append("username", $scope.regisUser.username);
		formData.append("password", $scope.regisUser.password);
		formData.append("fullName", $scope.regisUser.fullName);
		formData.append("email", $scope.regisUser.email);
		formData.append("avartar", document.forms['signupform'].file.files[0]);
		
		$http({
	        method: 'POST',
	        url: '/api/user/regis',
	        headers: {'Content-Type': undefined},
	        data: formData
     		})
     		.success(function(data, status) {   
     			$rootScope.regisSuccess = true;
     			ngProgress.complete();
				$location.path("/login");
     		})
     		.error(function(data, status){
				$scope.error = "Some error while regis user. Please try again!";
				$scope.haveError = true;
				ngProgress.complete();
			});
	};
	
	// get an user
	$scope.getUser = function() {
		ngProgress.start();
		$http.get('/api/user/' + $rootScope.user.username)
			.success(function(data){
				$scope.account = data.response;
				ngProgress.complete();
			})
			.error(function(data){
				ngProgress.complete();
				$location.path('/');
			});
	};
	
	$scope.$on('langBroadcast', function() {
		$translate.use(langService.key);
		$scope.lang = langService.key;
    });
});