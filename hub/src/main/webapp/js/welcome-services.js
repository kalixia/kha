'use strict';

var welcomeServices = angular.module('hub.welcome.services', ['hub.security.services', 'restangular']);

welcomeServices.factory('WelcomeService', ['Restangular', 'SecurityService', '$log',
    function welcomeServiceFactory(Restangular, SecurityService, $log) {
        return {
            installDone: function() {
                return Restangular.one('install', 'done').get().then(function(done) {
                    $log.debug("Installation done? " + done);
                    return done;
                });
            },
            installFor: function(user) {
                return Restangular.all('install').post(user).then(function(createdUser) {
                    SecurityService.setCurrentUser(createdUser);
                });
            }
        }
    }
]);
