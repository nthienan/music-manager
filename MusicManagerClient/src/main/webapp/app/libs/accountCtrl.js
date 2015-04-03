/**
 * @author nthienan
 */
//account controller
mainApp.controller('accountCtrl', function($rootScope, $scope, $http, $location) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	$scope.editState = false;
	$scope.changePassState = false;
	$scope.changePassSuccess = false;
	$scope.haveError = false;
	
	// get an user
	$scope.getUser = function() {
		$http.get('/api/user/' + $rootScope.user.username)
			.success(function(data){
				$scope.account = data.response;
			})
			.error(function(data){
				$location.path('/');
			});
	};
	
	//update account
	$scope.updateAccount = function() {
		$scope.account.fullName = $scope.fullNameNew;
		$http.put('/api/user', $scope.account)
			.success(function(data){
				$scope.editState = false;
			})
			.error(function(data){
				$scope.haveError = true;
				$scope.accountError = "Update accout error!";
			});
	};
	
	// edit button click
	$scope.editAccount = function() {
		$scope.fullNameNew = $scope.account.fullName;
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
		$http.put('/api/user/' + $rootScope.user.username + '/pass?oldPass=' + $scope.oldpassword + '&newPass=' + $scope.password)
			.success(function(data){
				if(data.statuscode === 200) {
					$scope.changePassSuccess = true;
					$scope.changePassState = false;
					delete $scope.oldpassword;
					delete $scope.password;
					delete $scope.passwordVerify;
				} else {
					$scope.changePassSuccess = false;
					$scope.changePassState = true;
					$scope.haveError = true;
					$scope.accountError = data.message;
				}
			})
			.error(function(data){
				$scope.changePassSuccess = false;
				$scope.changePassState = true;
				$scope.haveError = true;
				$scope.accountError = 'Some error while change pass!'
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
		$http.delete('/api/user/' + username)
			.success(function(data){
				$rootScope.logout();
			})
			.error(function(data, status){
				alert(data)
			});
	};
	
	$scope.getUser();
});