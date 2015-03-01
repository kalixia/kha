'use strict';

hubApp.factory('SecurityService', function (Restangular, $log) {
    var currentUser;

    function setCurrentUser(user) {
        $log.debug("Current user is now:");
        $log.debug(user);
        currentUser = user;
        if (user != null) {
            var accessToken = user.oauthTokens[0].accessToken;
            $log.debug("Access token is: " + accessToken);
            $httpProvider.defaults.headers.common['Authorization'] = "Bearer " + accessToken;
        }
    };

    return {
        setCurrentUser: setCurrentUser,
        getCurrentUser: function() {
            return currentUser;
        },
        login: function(username, password) {
            var data = $.param([
                { name: "username", value: username },
                { name: "password", value: password }
            ]);
            return Restangular.one('', '')
                .customPOST(data, 'login', {})
                .then(function(user) {
                    setCurrentUser(user);
                });
        },
        logout: function() {
            return Restangular.one('', '')
                .customPOST({}, 'logout', {})
                .then(function() {
                    this.setCurrentUser(null);
                });
        }
    }
});
