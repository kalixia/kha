'use strict';

var securityServices = angular.module('hub.security.services', ['ngResource']);

securityServices
    .factory('SecurityService', ['$log',
        function securityServiceFactory($log) {
            var currentUser;

            return {
                setCurrentUser: function(user) {
                    $log.debug("Current user is now:");
                    $log.debug(user);
                    currentUser = user;
                    if (user != null) {
                        var accessToken = user.oauthTokens[0].accessToken;
                        $log.debug("Access token is: " + accessToken);
                        securityServices.$httpProvider.defaults.headers.common['Authorization'] = "Bearer " + accessToken;
                    }
                },
                getCurrentUser: function() {
                    return currentUser;
                }
            }
        }
    ])
    .config(function ($httpProvider) {
        securityServices.$httpProvider = $httpProvider
    }
);
