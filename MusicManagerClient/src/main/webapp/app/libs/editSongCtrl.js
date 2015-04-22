/**
 * @author nthienan
 */
// edit song controller
mainApp.controller('editSongCtrl', function($rootScope, $scope, $http, $location, $routeParams, ngProgress) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get a song by id to edit
	$scope.load = function() {
		ngProgress.start();
		$http.get('/api/song/' + $rootScope.user.username + '/' + $routeParams.id)
			.success(function(data){
				$scope.song = data.response;
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// update
	$scope.update = function() {
		ngProgress.start();
		$http.put('/api/song/' + $rootScope.user.username, $scope.song)
			.success(function(data) {
				$location.path('/');
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// delete a song
	$scope.deleteSong = function(songId){
		ngProgress.start();
		$http.delete('/api/song/' + $rootScope.user.username + '/' + songId)
			.success(function(data){
				$location.path('/');
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	$scope.back = function() {
		$location.path('/');
	};
	
	// load data
	$scope.load();
});