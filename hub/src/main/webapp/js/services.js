'use strict';

angular.module('hub.services', ['hub.users.services', 'hub.devices.services', 'ngResource', 'restangular'])
    .config(function(RestangularProvider) {
        RestangularProvider.setBaseUrl("http://localhost:8082");
    });
