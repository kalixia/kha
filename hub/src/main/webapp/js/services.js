'use strict';

angular.module('hub.services', ['hub.welcome.services', 'hub.users.services', 'hub.devices.services', 'hub.security.services',
                                'ngResource', 'restangular'])
    .config(function(RestangularProvider) {
        RestangularProvider.setBaseUrl("http://localhost:8082");
    });
