/**
 * @author nthienan
 */
// song controller
mainApp.controller('songCtrl', function($rootScope, $scope, $http, $location, $filter, ngProgress) {
	$scope.selectedId = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	// sort
	var orderBy = $filter('orderBy');
	$scope.order = function(predicate, reverse) {
		$rootScope.songResponse.content = orderBy($rootScope.songResponse.content, predicate, reverse);
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
		$http.get('/api/song/' + $rootScope.user.username + '?page=' + ($rootScope.pageNumber - 1) + '&size=' + $rootScope.pageSize)
			.success(function(data, status) {
				$rootScope.songResponse = data.response;
				ngProgress.complete();
			})
			.error(function(data, status){
				console.log(status + data);
				ngProgress.complete();
			});
	};

	// save a song into database
	$scope.create = function() {
		ngProgress.start();
		if($scope.formData.shared == undefined)
			$scope.formData.shared = false;
		var formData = new FormData();
		formData.append("name", $scope.formData.name);
		formData.append("gener", $scope.formData.gener);
		formData.append("artist", $scope.formData.artist);
		formData.append("shared", $scope.formData.shared);
		formData.append("musician", $scope.formData.musician);
		formData.append("file", document.forms['formUpload'].file.files[0]);
		$http({
		        method: 'POST',
		        url: '/api/song/' + $rootScope.user.username + '/upload',
		        headers: {'Content-Type': undefined},
		        data: formData
	     })
	    .success(function(data, status) {   
	    	$location.path('/');
	     })
	     .error(function(data, status){
	    	 ngProgress.complete();
	     });
	};
	
	// delete
	$scope.deleteMulti = function(){
		ngProgress.start();
		var values = JSON.stringify($scope.selectedId);
		jQuery.ajax({
			headers : {
				'Accept' : 'application/json',
				'Content-Type' : 'application/json'},
				'type' : 'DELETE',
				'url' : '/api/song/' + $rootScope.user.username,
				'data' : values,
				'dataType' : 'json'
			})
			.success(function(data){
				ngProgress.complete();
				$scope.load();
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
				ngProgress.complete();
				$location.path('/play-song/' + id);
			})
			.error(function(data, status){
				ngProgress.complete();
			});
	};
	
	// push or splice selected id
	$scope.select = function(id){
		var idx = $scope.selectedId.indexOf(id);
		if(idx > -1)
			$scope.selectedId.splice(idx, 1);
		else
			$scope.selectedId.push(id);
	};

	// redirect to edit view
	$scope.editSong = function(id) {
		$location.path('/edit-song/' + id);
	};
	
	$scope.back = function() {
		$location.path('/');
	};
	
	$scope.load();
});