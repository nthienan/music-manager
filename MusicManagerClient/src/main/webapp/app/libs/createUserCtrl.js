/**
 * @author nthienan
 */
// create user controller
mainApp.controller('createUserCtrl', function($scope, $http, $location, ngProgress, $translate, langService){
	$scope.selectedRole = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	// create new user
	$scope.createUser = function(){
		ngProgress.start();
		$scope.regisUser.password = $scope.password;
		var formData = new FormData();
		formData.append("username", $scope.regisUser.username);
		formData.append("password", $scope.regisUser.password);
		formData.append("fullName", $scope.regisUser.fullName);
		formData.append("email", $scope.regisUser.email);
		formData.append("role", $scope.selectedRole);
		formData.append("avartar", document.forms['signupform'].file.files[0]);
		
		$http({
	        method: 'POST',
	        url: '/api/user/regis',
	        headers: {'Content-Type': undefined},
	        data: formData
     		})
     		.success(function(data, status) {   
     			$location.path("/admin");
     			ngProgress.complete();
     		})
     		.error(function(data){
     			$scope.error = "Some error while add user. Please try again!";
				$scope.haveError = true;
				ngProgress.complete();
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
	
	$scope.$on('langBroadcast', function() {
		$translate.use(langService.key);
		$scope.lang = langService.key;
    });
});