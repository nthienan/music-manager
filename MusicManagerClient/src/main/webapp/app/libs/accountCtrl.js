/**
 * @author nthienan
 */
//account controller
mainApp.controller('accountCtrl', function($rootScope, $scope, $http, $location, ngProgress) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	$scope.editState = false;
	$scope.changePassState = false;
	$scope.changePassSuccess = false;
	$scope.haveError = false;
	
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
	
	//update account
	$scope.updateAccount = function() {
		ngProgress.start();
		$scope.account.fullName = $scope.fullNameNew;
		$scope.account.email = $scope.emailNew;
		$http.put('/api/user', $scope.account)
			.success(function(data, status){
				$scope.editState = false;
				$rootScope.getUser();
				ngProgress.complete();
			})
			.error(function(data, status){
				$scope.haveError = true;
				$scope.accountError = "Update accout error!";
				ngProgress.complete();
			});
	};
	
	// edit button click
	$scope.editAccount = function() {
		$scope.fullNameNew = $scope.account.fullName;
		$scope.emailNew = $scope.account.email;
		$scope.editState = true;
	};
	
	// cancel button click
	$scope.cancelAccount = function() {
		$scope.editState = false;
	}
	
	// change pass link click
	$scope.changePass = function() {
		$scope.changePassState = true;
	}
	
	// change pass button click
	$scope.updatePass = function() {
		ngProgress.start();
		$http.put('/api/user/' + $rootScope.user.username + '/pass?oldPass=' + $scope.oldpassword + '&newPass=' + $scope.password)
			.success(function(data){
				if(data.statuscode === 200) {
					$scope.changePassSuccess = true;
					$scope.changePassState = false;
					delete $scope.oldpassword;
					delete $scope.password;
					delete $scope.passwordVerify;
					ngProgress.complete();
				} else {
					$scope.changePassSuccess = false;
					$scope.changePassState = true;
					$scope.haveError = true;
					$scope.accountError = data.message;
					ngProgress.complete();
				}
			})
			.error(function(data){
				$scope.changePassSuccess = false;
				$scope.changePassState = true;
				$scope.haveError = true;
				$scope.accountError = 'Some error while change pass!';
				ngProgress.complete();
			});
	}
	
	// change pass button cancel click
	$scope.cancelPass = function() {
		$scope.changePassState = false;
		delete $scope.oldpassword;
		delete $scope.password;
		delete $scope.passwordVerify;
	}
	
	// delete an user
	$scope.deleteOne = function(username){
		ngProgress.start();
		$http.delete('/api/user/' + username)
			.success(function(data){
				$rootScope.logout();
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
				alert(data)
			});
	};
	
	$scope.back = function() {
		$location.path('/');
	};
	
	$scope.getUser();
});