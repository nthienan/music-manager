/**
 * @author nthienan
 */
// share controller
mainApp.controller('shareCtrl', function($rootScope, $scope, $http, $location, $filter, $window, ngProgress, $translate, langService) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	// sort
	var orderBy = $filter('orderBy');
	$scope.order = function(predicate, reverse) {
		$rootScope.shareResponse.content = orderBy($rootScope.shareResponse.content, predicate, reverse);
		switch(predicate){
			case 'id': 
				$scope.id = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.name = '';
				$scope.gener = '';
				$scope.musician = '';
				$scope.artist = '';
				break;
			case 'name': 
				$scope.name = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.id = '';
				$scope.gener = '';
				$scope.musician = '';
				$scope.artist = '';
				break;
			case 'gener': 
				$scope.gener = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.name = '';
				$scope.id = '';
				$scope.musician = '';
				$scope.artist = '';
				break;
			case 'musician': 
				$scope.musician = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.name = '';
				$scope.id = '';
				$scope.gener = '';
				$scope.artist = '';
				break;
			case 'artist': 
				$scope.artist = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.name = '';
				$scope.id = '';
				$scope.gener = '';
				$scope.musician = '';
				break;
		}
	};

	// load list
	$scope.load = function() {
		ngProgress.start();
		$http.get('/api/song/share?page=' + ($rootScope.pageNumber - 1) + '&size=' + $rootScope.pageSize)
			.success(function(data) {
				$rootScope.shareResponse = data.response;
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// play
	$scope.playSong = function(id) {
		ngProgress.start();
		$http.put('/api/song/' + id + '/view')
			.success(function(data) {
				$location.path('/play-song/' + id);
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	//download
	$scope.downloadSong = function(id){
		ngProgress.start();
		$http.put('/api/song/' + id + '/download')
			.success(function(data){
				$window.open('/api/song/' + id + '/download', '_blank');
				ngProgress.complete();
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	$scope.$on('langBroadcast', function() {
		$translate.use(langService.key);
		$scope.lang = langService.key;
    });
	
	$scope.load();
});