'use strict';

var usersServices = angular.module('hub.users.services', ['restangular']);

usersServices.factory('UserService', ['Restangular', function userServiceFactory(Restangular) {
    return {
        getUser: function(username) {
            return Restangular.one('', username).get();
        },
        createUser: function(user) {
            return Restangular.all('').post(user);
        },
        installDone: function() {
            return Restangular.one('admin/users', 'count').get() > 0;
        }
    }
}]);
