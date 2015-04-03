/**
 * @author nthienan
 */
// play controller
mainApp.controller('playCtrl', function($rootScope, $scope, $http, $location, $routeParams, $window) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get a song by id to play
	$scope.load = function() {
		$http.get('/api/song/' + $rootScope.user.username + '/' + $routeParams.id)
			.success(function(data){
				$scope.song = data.response;
		});
	};
	
	// download file
	$scope.downloadSong = function(id) {
		$http.put('/api/song/' + id + '/download')
			.success(function(data){
				$scope.song = data.response;
				$window.open('/api/song/' + id + '/download', '_blank');
		});
	};
	
	// load data
	$scope.load();
});