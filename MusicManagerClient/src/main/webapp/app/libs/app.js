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

var mainApp = angular.module('musicManagerApp', ['ngRoute', 'ngSanitize', 'ngCookies', 'ngProgress']);

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
		// share list
		.when('/share', {
			templateUrl : 'views/shareList.html',
			controller : 'shareCtrl'
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
		// active
		.when('/active/:username/:token', {
			templateUrl : 'views/active.html',
			controller : 'activeCtrl'
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
 	.run(function($rootScope, $location, $cookieStore, $http, ngProgress) {
 		$rootScope.pageNumber = 1;
 		$rootScope.pageSize = 10;
 		$rootScope.activeStatus = false;
 		/* Reset error when a new view is loaded */
 		$rootScope.$on('$viewContentLoaded', function() {
 			delete $rootScope.error;
 		});
		
 		$rootScope.hasRole = function(role) {
 			if ($rootScope.user == undefined) {
 				return false;
 			}
			
 			for(var i = 0; i < $rootScope.user.roles.length; i++) {
	 			if ($rootScope.user.roles[i] == role)
	 				return true;
 			}
			
 			return false;
 		};
 		
 		$rootScope.logout = function() {
 			ngProgress.start();
 			$http.get('/api/logout')
				.success(function(data){
					delete $rootScope.user;
		 			delete $rootScope.authToken;
		 			$rootScope.authenticated = false;
		 			$cookieStore.remove('authToken');
		 			ngProgress.complete();
		 			$location.path("/login");
				})
				.error(function(data, status){
					ngProgress.complete();
					alert(status)
				});
 			
 		};
 		
 		$rootScope.getUser = function (){
 			ngProgress.start();
 			$http.get('/api/user/get')
				.success(function(data, status){
					$rootScope.user = data.response;
					ngProgress.complete();
				})
				.error(function(data, status){
					console.log(data + status);
					ngProgress.complete();
				});
 		}
		
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
 				})
 				.error(function(data, status){
 					console.log(data + status);
 				});
 		}
		
 		$rootScope.initialized = true;
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