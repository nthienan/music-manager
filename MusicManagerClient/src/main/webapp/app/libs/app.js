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

var mainApp = angular.module('musicManagerApp', ['ngRoute', 'ngSanitize', 'ngCookies', 'ngProgress', 'pascalprecht.translate']);

mainApp.config([ '$routeProvider', '$locationProvider', '$httpProvider', '$translateProvider', function($routeProvider, $locationProvider, $httpProvider, $translateProvider) {
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
	
	$translateProvider.translations('en', {
		"Main" : {
			"Title" : "Music Manager",
			"Thesis" : "Thesis 2015",
			"Language" : "Ngôn ngữ",
			"MyMusic" : "My Music",
			"ShareWithMe" : "Share With Me",
			"Setting" : "Setting",
			"ManageUsers" : "Manage Users",
			"Statistics" : "Statistics",
			"Search" : "Search",
			"Logout" : "Logout",
			"TotalItems" : "Total items",
			"TotalPages" : "Total pages",
			"Show" : "Show",
			"To" : "to",
			"PageSize" : "Page size",
			"Page" : "Page"
		},
		"Song" : {
			"No" : "No",
			"Name" : "Name",
			"Musician" : "Musician",
			"Artist" : "Artist",
			"Gener" : "Gener",
			"Actions" : "Actions",
			"Shared" : "Shared",
			"File" : "File",
			"AddNewSong" : "Add New Song",
			"EditSong" : "Edit Song",
			"LastUpdate" : "Last update",
			"Song" : "Song",
			"Listen" : "Listen",
			"Download" : "Download",
			"MostListened" : "Most Listened",
			"MostDownload" : "Most Download",
			"Message" : {
				"Shared" : "Share with other users",
				"ViewDetails" : "View Details",
				"Listen" : "Listen",
				"Download" : "Download"
			}
		},
		"Account" : {
			"Name" : "Full name",
			"Email" : "Email",
			"Username" : "Username",
			"Role" : "Roles",
			"Password" : "Password",
			"ChangePassword" : "Change password",
			"OldPass" : "Old pass",
			"NewPass" : "New pass",
			"PassVerify" : "Pass Verify",
			"AccountInformation" : "Account Information",
			"AddUser" : "Add User",
			"Avatar" : "Avatar",
			"EditUser" : "Edit User",
			"Actived" : "Actived",
			"NotActive" : "Not active",
			"ListUser" : "List User",
			"No" : "No",
			"Actions" : "Actions",
			"Button" : {
				"ChangeAvatar" : "Change",
				"RemoveAvatar" : "Remove",
				"SelectAvatar" : "Select image",
				"ChangePass" : "Change",
				"DeleteAccount" : "Delete account",
				"ResetPass" : "Reset password",
				"Edit" : "Edit",
				"Reset" : "Reset"
			},
			"Message" : {
				"Success" : "Successful",
				"ChangePassSuccess" : "Password changed successful."
			}
		},
		"Button" : {
			"Back" : "Back",
			"Play" : "Play",
			"Edit" : "Edit",
			"Delete" : "Delete",
			"Cancel" : "Cancel",
			"Download" : "Download",
			"Update" : "Update",
			"Save" : "Save",
			"Listen" : "Listen"
		},
		"Message" : {
			"Required" : "This field is required",
			"PassTooShort" : "Password is too short",
			"PassNotMatch" : "Password do not match",
			"Invalid" : "Value invalid",
			"UsernameTooShort" : "User name too short"
		}
	  })
	  .translations('vi', {
		  "Main" : {
			  "Title" : "Quản Lý Nhạc",
			  "Thesis" : "Luận văn 2015",
			  "Language" : "Language",
			  "MyMusic" : "Nhạc Của Tôi",
			  "ShareWithMe" : "Chia Sẽ Với Tôi",
			  "Setting" : "Cài Đặt",
			  "ManageUsers" : "Quản Lý",
			  "Statistics" : "Thống Kê",
			  "Search" : "Tìm Kiếm",
			  "Logout" : "Đăng xuất",
			  "TotalItems" : "Tổng số mục",
			  "TotalPages" : "Tổng số trang",
			  "Show" : "Hiển thị từ",
			  "To" : "đến",
			  "PageSize" : "Hiển thị",
			  "Page" : "Trang"
		},
		"Account" : {
			"Name" : "Tên",
			"Email" : "Email",
			"Username" : "Tên người dùng",
			"Role" : "Quyền",
			"Password" : "Mật khẩu",
			"ChangePassword" : "Đổi mật khẩu",
			"OldPass" : "Mật khẩu hiện tại",
			"NewPass" : "Mật khẩu mới",
			"PassVerify" : "Nhập lại mật khẩu",
			"AccountInformation" : "Thông Tin Tài Khoản",
			"AddUser" : "Người Dùng Mới",
			"Avatar" : "Ảnh đại diện",
			"EditUser" : "Cập Nhật Người Dùng",
			"Actived" : "Đã kích hoạt",
			"NotActive" : "Chưa kích hoạt",
			"ListUser" : "Danh Sách Người Dùng",
			"No" : "STT",
			"Actions" : "Thao tác",
			"Button" : {
				"ChangeAvatar" : "Đổi ảnh đại diện",
				"RemoveAvatar" : "Xóa ảnh đại diện",
				"SelectAvatar" : "Chọn ảnh",
				"ChangePass" : "Cập nhật",
				"DeleteAccount" : "Xóa tài khoản",
				"ResetPass" : "Khôi phục mật khẩu",
				"Edit" : "Sửa",
				"Reset" : "Khôi phục"
			},
			"Message" : {
				"Success" : "Thành công",
				"ChangePassSuccess" : "Mật khẩu đã thay đổi thành công."
			}
		},
		"Song" : {
			"No" : "STT",
			"Name" : "Tên",
			"Musician" : "Sáng tác",
			"Artist" : "Thể hiện",
			"Gener" : "Loại",
			"Actions" : "Thao tác",
			"Shared" : "Chia sẻ",
			"File" : "Tệp",
			"AddNewSong" : "Bài Hát Mới",
			"EditSong" : "Cập Nhật Thông Tin Bài Hát",
			"LastUpdate" : "Cập nhật lúc",
			"Song" : "Bài hát",
			"Listen" : "Lượt nghe",
			"Download" : "Lượt tải về",
			"MostListened" : "Nghe nhiều nhất",
			"MostDownload" : "Tải về nhiều nhất",
			"Message" : {
				"Shared" : "Chia sẽ với người dùng khác",
				"ViewDetails" : "Xem chi tiết",
				"Listen" : "Nghe",
				"Download" : "Tải về"
			}
		},
		"Button" : {
			"Back" : "Trở về",
			"Play" : "Phát",
			"Edit" : "Sửa",
			"Delete" : "Xóa",
			"Cancel" : "Hủy",
			"Download" : "Tải xuống",
			"Update" : "Cập nhật",
			"Save" : "Lưu",
			"Listen" : "Nghe"
		},
		"Message" : {
			"Required" : "Bắt buộc",
			"PassTooShort" : "Mật khẩu quá ngắn",
			"PassNotMatch" : "Mật khẩu không khớp",
			"Invalid" : "Giá trị không hợp lệ",
			"UsernameTooShort" : "Tên người dùng quá ngắn"
		}
	  });
	
	  $translateProvider.preferredLanguage('en');
	
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

mainApp.factory('langService', function($rootScope) {
    var langService = {};
    
    langService.key = 'en';

    langService.prepForBroadcast = function(msg) {
        this.key = msg;
        this.broadcastItem();
    };

    langService.broadcastItem = function() {
        $rootScope.$broadcast('langBroadcast');
    };

    return langService;
});