'use strict';

hubApp.factory('WelcomeService', function (Restangular, SecurityService, $log) {
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
);
