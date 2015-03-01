'use strict';

hubApp.factory('UserService', function (Restangular) {
    return {
        getUser: function(username) {
            return Restangular.one('', username).get();
        },
        createUser: function(user) {
            return Restangular.all('').post(user);
        }
    }
});
