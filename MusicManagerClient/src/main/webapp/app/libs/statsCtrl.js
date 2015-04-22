/**
 * @author nthienan
 */
// statistics controller
mainApp.controller('statsCtrl', function($rootScope, $scope, $http, $location, $window, ngProgress) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	$scope.load = function() {
		ngProgress.start();
		$http.get('/api/user/' + $rootScope.user.username + '/statistics')
			.success(function(data){
				$scope.stats = data.response;
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
				alert(status + data);
			});
	};
	
	// redirect to listen view
	$scope.listen = function(songIdMaxView) {
		ngProgress.start();
		$http.put('/api/song/' + songIdMaxView + '/view')
			.success(function(data) {
				ngProgress.complete();
				$location.path('/play-song/' + songIdMaxView);
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// download file
	$scope.download = function(songIdMaxDownLoad) {
		ngProgress.start();
		$http.put('/api/song/' + songIdMaxDownLoad + '/download')
			.success(function(data){
				$scope.load();
				$window.open('/api/song/' + songIdMaxDownLoad + '/download', '_blank');
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	$scope.load();
});