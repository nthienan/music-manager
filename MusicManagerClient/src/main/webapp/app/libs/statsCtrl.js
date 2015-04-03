/**
 * @author nthienan
 */
// statistics controller
mainApp.controller('statsCtrl', function($rootScope, $scope, $http, $location, $window) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	$scope.load = function() {
		$http.get('/api/user/' + $rootScope.user.username + '/statistics')
			.success(function(data){
				$scope.stats = data.response;
			})
			.error(function(data, status){
				alert(status + data);
			});
	};
	
	// redirect to listen view
	$scope.listen = function(songIdMaxView) {
		$http.put('/api/song/' + songIdMaxView + '/view')
			.success(function(data) {
				$location.path('/play-song/' + songIdMaxView);
		});
	};
	
	// download file
	$scope.download = function(songIdMaxDownLoad) {
		$http.put('/api/song/' + songIdMaxDownLoad + '/download')
			.success(function(data){
				$scope.load();
				$window.open('/api/song/' + songIdMaxDownLoad + '/download', '_blank');
		});
	};
	
	$scope.load();
});