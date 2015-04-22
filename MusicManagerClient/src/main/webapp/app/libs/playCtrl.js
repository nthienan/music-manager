/**
 * @author nthienan
 */
// play controller
mainApp.controller('playCtrl', function($rootScope, $scope, $http, $location, $routeParams, $window, ngProgress) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get a song by id to play
	$scope.load = function() {
		ngProgress.start();
		$http.get('/api/song/' + $rootScope.user.username + '/' + $routeParams.id)
			.success(function(data){
				$scope.song = data.response;
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// download file
	$scope.downloadSong = function(id) {
		ngProgress.start();
		$http.put('/api/song/' + id + '/download')
			.success(function(data){
				$scope.song = data.response;
				$window.open('/api/song/' + id + '/download', '_blank');
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// load data
	$scope.load();
});