/**
 * @author nthienan
 */
//main controller
mainApp.controller('mainCtrl', function($rootScope, $scope, $http, $location) {
	$rootScope.authenticated = false;
	
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// search
	$scope.search = function(){
		if($scope.keyword != ''){
			$http.get('/api/song/' + $rootScope.user.username + '/search?keyword=' + $scope.keyword + '&size=' + $rootScope.pageSize)
				.success(function(data){
					$rootScope.songResponse = data.response;
			});
		}else{
			$http.get('/api/song/' + $rootScope.user.username + '/search?keyword=' + $scope.keyword + '&size=' + $rootScope.pageSize + '&page=' + ($rootScope.pageNumber - 1))
				.success(function(data){
					$rootScope.songResponse = data.response;
			});
		}
	};
	
	// redirect to list view
	$scope.showList = function() {
		$location.path('/');
	};

	// redirect to add view
	$scope.addSong = function() {
		$location.path('/add-song');
	};
	
	// redirect to account manager
	$scope.setting = function() {
		$location.path('/account');
	};
	
	// redirect to statistics view
	$scope.statistics = function() {
		$location.path('/statistics');
	};
	
	// redirect to share view
	$scope.shareList = function() {
		$location.path('/share');
	};
});