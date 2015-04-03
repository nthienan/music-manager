/**
 * @author nthienan
 */
// edit song controller
mainApp.controller('editSongCtrl', function($rootScope, $scope, $http, $location, $routeParams) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get a song by id to edit
	$scope.load = function() {
		$http.get('/api/song/' + $rootScope.user.username + '/' + $routeParams.id)
			.success(function(data){
				$scope.song = data.response;
		});
	};
	
	// update
	$scope.update = function() {
		$http.put('/api/song/' + $rootScope.user.username, $scope.song)
			.success(function(data) {
				$location.path('/');
		});
	};
	
	// delete a song
	$scope.deleteSong = function(songId){
		$http.delete('/api/song/' + $rootScope.user.username + '/' + songId)
			.success(function(data){
				$location.path('/');
		});
	};
	
	// load data
	$scope.load();
});