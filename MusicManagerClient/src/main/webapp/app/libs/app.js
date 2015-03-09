/**
 * @author nthienan
 */
var exampleAppConfig = {
	/*
	 * When set to false a query parameter is used to pass on the auth token.
	 * This might be desirable if headers don't work correctly in some
	 * environments and is still secure when using https.
	 */
	useAuthTokenHeader: true 	
};

var mainApp = angular.module('musicManagerApp', ['ngRoute', 'ngSanitize', 'ngCookies']);

mainApp.config([ '$routeProvider', '$locationProvider', '$httpProvider', function($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider
		// login
		.when('/login', {
			templateUrl : 'views/login.html',
			controller : 'loginCtrl'
		})
		// sign up
		.when('/sign-up', {
			templateUrl : 'views/signup.html',
			controller : 'userCtrl'
		})
		// account manager
		.when('/account', {
			templateUrl : 'views/accountView.html',
			controller : 'accountCtrl'
		})
		// list
		.when('/', {
			templateUrl : 'views/listSong.html',
			controller : 'songCtrl'
		})
		// add
		.when('/add-song', {
			templateUrl : 'views/addSong.html',
			controller : 'songCtrl'
		})
		// edit
		.when('/edit-song/:id', {
			templateUrl : 'views/editSong.html',
			controller : 'editSongCtrl'
		})
		// play view
		.when('/play-song/:id', {
			templateUrl : 'views/playSong.html',
			controller : 'playCtrl'
		})
		// statistics
		.when('/statistics', {
			templateUrl : 'views/statistics.html',
			controller : 'statsCtrl'
		})
		// list user
		.when('/admin', {
			templateUrl : 'views/listUser.html',
			controller : 'adminCtrl'
		})
		// add user
		.when('/admin/add-user', {
			templateUrl : 'views/addUser.html',
			controller : 'createUserCtrl'
		})
		// edit user
		.when('/admin/edit-user/:username', {
			templateUrl : 'views/editUser.html',
			controller : 'editUserCtrl'
		})
		// default
		.otherwise({
			redirectTo : '/login'
		});

//	$locationProvider.hashPrefix('!');
	
	/*
	 * Register error provider that shows message on failed requests or
	 * redirects to login page on unauthenticated requests
	 */
	$httpProvider.interceptors.push(function ($q, $rootScope, $location) {
		return {
			'responseError': function(rejection) {
				var status = rejection.status;
				var config = rejection.config;
				var method = config.method;
				var url = config.url;
	      
				if (status == 401 || status == 403) {
					$rootScope.authenticated = false;
					$location.path( "/login" );
				} else {
					$rootScope.error = method + " on " + url + " failed with status " + status;
				}
				return $q.reject(rejection);
			}
		};
	});
    
    /*
	 * Registers auth token interceptor, auth token is either passed by header
	 * or by query parameter as soon as there is an authenticated user
	 */
	$httpProvider.interceptors.push(function ($q, $rootScope, $location) {
		return {
			'request': function(config) {
				var isRestCall = config.url.indexOf('/api') == 0;
				if (isRestCall && angular.isDefined($rootScope.authToken)) {
					var authToken = $rootScope.authToken;
					if (exampleAppConfig.useAuthTokenHeader) {
						config.headers['X-Auth-Token'] = authToken;
					} else {
						config.url = config.url + "?token=" + authToken;
					}
				}
				return config || $q.when(config);
			}
		};
	});
}])
 	.run(function($rootScope, $location, $cookieStore, $http) {
 		$rootScope.pageNumber = 1;
 		$rootScope.pageSize = 10;
 		/* Reset error when a new view is loaded */
 		$rootScope.$on('$viewContentLoaded', function() {
 			delete $rootScope.error;
 		});
		
 		$rootScope.hasRole = function(role) {
 			if ($rootScope.user == undefined) {
 				return false;
 			}
			
 			if ($rootScope.user.roles[role] == undefined) {
 				return false;
 			}
			
 			return $rootScope.user.roles[role];
 		};
 		
 		$rootScope.logout = function() {
 			$http.get('/api/logout')
				.success(function(data){
					delete $rootScope.user;
		 			delete $rootScope.authToken;
		 			$rootScope.authenticated = false;
		 			$cookieStore.remove('authToken');
		 			$location.path("/login");
				})
				.error(function(data, status){
					alert(status)
				});
 			
 		};
		
 		/* Try getting valid user from cookie or go to login page */
 		var originalPath = $location.path();
 		$location.path("/login");
 		var authToken = $cookieStore.get('authToken');
 		if (authToken != undefined) {
 			$rootScope.authToken = authToken;
 			
 			$http.get('/api/user/get')
 				.success(function(data){
 					$rootScope.user = data.response;
 					$rootScope.authenticated = true;
 	 				$location.path(originalPath);
 				});
 		}
		
 		$rootScope.initialized = true;
 	});

// login controller
mainApp.controller('loginCtrl', function($rootScope, $scope, $http, $location, $cookieStore) {
	$scope.rememberMe = true;
	$scope.haveError = false;
	
	$scope.showSignUp = function() {
		$location.path("/sign-up");
	}
	
	$scope.login = function() {
		$http({
			method: 'POST',
			url: '/api/user/authenticate/' + $scope.username + '/' + $scope.password,
			headers : {'Content-Type': 'application/x-www-form-urlencoded'},
		})
		.success(function(data, status) {   
			var authToken = data.token;
			$scope.haveError = false;
			$rootScope.authToken = authToken;
			
			$cookieStore.put('authToken', authToken);
			$http.get('/api/user/get')
 				.success(function(data){
 					$rootScope.user = data.response;
 					$rootScope.authenticated = true;
 					$location.path("/");
 				});
		})
		.error(function(data, status) {
			$scope.haveError = true;
			$rootScope.error = "Username/password is incorect";
		});
	};
});

//user controller
mainApp.controller('userCtrl', function($rootScope, $scope, $http, $location) {
	$scope.haveError = false;
	
	// create new user
	$scope.createUser = function(){
		$scope.regisUser.password = $scope.password;
		$http.post('/api/user/regis', $scope.regisUser)
			.success(function(data){
				$rootScope.regisSuccess = true;
				$location.path("/login");
			})
			.error(function(data){
				$scope.error = "Some error while regis user. Please try again!";
				$scope.haveError = true;
			});
	};
	
	// get an user
	$scope.getUser = function() {
		$http.get('/api/user/' + $rootScope.user.username)
			.success(function(data){
				$scope.account = data.response;
			})
			.error(function(data){
				$location.path('/');
			});
	}
});

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
});

// song controller
mainApp.controller('songCtrl', function($rootScope, $scope, $http, $location, $filter) {
	$scope.selectedId = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	// sort
	var orderBy = $filter('orderBy');
	$scope.order = function(predicate, reverse) {
		$scope.response.content = orderBy($scope.response.content, predicate, reverse);
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
		$http.get('/api/song/' + $rootScope.user.username + '?page=' + ($rootScope.pageNumber - 1) + '&size=' + $rootScope.pageSize)
			.success(function(data) {
				$rootScope.songResponse = data.response;
			});
	};

	// save a song into database
	$scope.create = function() {
		var formData = new FormData();
		formData.append("name", $scope.formData.name);
		formData.append("gener", $scope.formData.gener);
		formData.append("artist", $scope.formData.artist);
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
	     });
	};
	
	// delete
	$scope.deleteMulti = function(){
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
				$scope.load();
		});
	};
	
	// play
	$scope.playSong = function(id) {
		$http.put('/api/song/' + $rootScope.user.username + '/' + id + '/view')
			.success(function(data) {
				$location.path('/play-song/' + id);
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
	
	$scope.load();
});

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
		$http.put('/api/song/' + $rootScope.user.username + '/' + id + '/download')
			.success(function(data){
				$scope.song = data.response;
				$window.open('/api/song/' + id + '/download', '_blank');
		});
	};
	
	// load data
	$scope.load();
});

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

// admin controller
mainApp.controller('adminCtrl', function($rootScope, $scope, $http, $location, $filter) {
	$scope.selectedId = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	// sort
	var orderBy = $filter('orderBy');
	$scope.order = function(predicate, reverse) {
		$scope.response.content = orderBy($scope.response.content, predicate, reverse);
		switch(predicate){
			case 'username': 
				$scope.username = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.name = '';
				break;
			case 'name': 
				$scope.name = reverse ? 'glyphicon-sort-by-attributes-alt' : 'glyphicon-sort-by-attributes';
				$scope.username = '';
				break;
		}
	};

	// load list
	$scope.load = function() {
		$http.get('/api/user' + '?page=' + ($rootScope.pageNumber - 1) + '&size=' + $rootScope.pageSize)
			.success(function(data) {
				$scope.response = data.response;
			});
	};
	
	// delete
	$scope.deleteMulti = function(){
		var values = JSON.stringify($scope.selectedId);
		jQuery.ajax({
			headers : {
				'Accept' : 'application/json',
				'Content-Type' : 'application/json'},
				'type' : 'DELETE',
				'url' : '/api/user',
				'data' : values,
				'dataType' : 'json'
			})
			.success(function(data){
				$scope.load();
		});
	};
	
	// push or splice selected id
	$scope.select = function(username){
		var idx = $scope.selectedId.indexOf(username);
		if(idx > -1)
			$scope.selectedId.splice(idx, 1);
		else
			$scope.selectedId.push(username);
	};
	
	// redirect to edit view
	$scope.editUser = function(username) {
		$location.path('/admin/edit-user/' + username);
	};
	
	// redirect to add user view
	$scope.addUser = function() {
		$location.path('/admin/add-user');
	};
	
	$scope.load();
});

// create user controller
mainApp.controller('createUserCtrl', function($scope, $http, $location){
	$scope.selectedRole = [];
	$http.defaults.headers.post['Content-Type'] = 'application/json';
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	// create new user
	$scope.createUser = function(){
		$scope.regisUser.password = $scope.password;
		$scope.regisUser.roles = $scope.selectedRole;
		$http.post('/api/user/regis', $scope.regisUser)
			.success(function(data){
				$location.path("/admin");
			})
			.error(function(data){
				$scope.error = "Some error while add user. Please try again!";
				$scope.haveError = true;
			});
	};
	
	// push or splice selected role
	$scope.selectRole = function(role){
		var idx = $scope.selectedRole.indexOf(role);
		if(idx > -1)
			$scope.selectedRole.splice(idx, 1);
		else
			$scope.selectedRole.push(role);
	};
});

//edit user controller
mainApp.controller('editUserCtrl', function($rootScope, $scope, $http, $location, $routeParams) {
	$http.defaults.headers.post['Content-Type'] = 'application/json';

	// get an user by username to edit
	$scope.load = function() {
		$http.get('/api/user/' + $routeParams.username)
			.success(function(data){
				$scope.editUser = data.response;
		});
	};
	
	// update
	$scope.updateUser = function() {
		$http.put('/api/user', $scope.editUser)
			.success(function(data) {
				$location.path('/admin');
		});
	};
	
	// delete an user
	$scope.deleteOne = function(username){
		$http.delete('/api/user/' + username)
			.success(function(data){
				$location.path('/admin');
			})
			.error(function(data, status){
				alert(data)
			});
	};
	
	$scope.showList = function() {
		$location.path('/admin');
	};
	
	// load data
	$scope.load();
});

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
		$location.path('/play-song/' + songIdMaxView);
	}
	
	// download file
	$scope.download = function(songIdMaxDownLoad) {
		$http.put('/api/song/' + $rootScope.user.username + '/' + songIdMaxDownLoad + '/download')
			.success(function(data){
				$scope.load();
				$window.open('/api/song/' + songIdMaxDownLoad + '/download', '_blank');
		});
	}
	
	$scope.load();
});

// Angular directive for confirm dialog box
mainApp.directive('ngConfirmClick', [
     function(){
         return {
             priority: 1,
             terminal: true,
             link: function (scope, element, attr) {
                 var msg = attr.ngConfirmClick || "Are you sure?";
                 var clickAction = attr.ngClick;
                 element.bind('click',function (event) {
                     if ( window.confirm(msg) ) {
                         scope.$eval(clickAction)
                     }
                 });
             }
         };
 }]);

// directive ng-match
mainApp.directive('pwCheck', [function () {
  return {
    require: 'ngModel',
    link: function (scope, elem, attrs, ctrl) {
      var firstPassword = '#' + attrs.pwCheck;
      elem.add(firstPassword).on('keyup', function () {
        scope.$apply(function () {
          var v = elem.val()===$(firstPassword).val();
          ctrl.$setValidity('pwmatch', v);
        });
      });
    }
  }
}]);

// high light
mainApp.filter('highlight', function () {
    return function (text, search, caseSensitive) {
        if (text && (search || angular.isNumber(search))) {
            text = text.toString();
            search = search.toString();
            if (caseSensitive) {
                return text.split(search).join('<span class="ui-match">' + search + '</span>');
            } else {
                return text.replace(new RegExp(search, 'gi'), '<span class="ui-match">$&</span>');
            }
        } else {
            return text;
        }
    };
});