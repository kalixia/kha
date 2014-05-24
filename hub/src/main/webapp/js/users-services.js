'use strict';

hubServices.factory('UserService', ['Restangular', function userServiceFactory(Restangular) {
    return {
        createUser: function(user) {
            return Restangular.all('').post(user);
        },
        installDone: function() {
            return Restangular.one('admin/users', 'count').then(function(count) {
                return count > 0;
            });
        }
    }
}]);
