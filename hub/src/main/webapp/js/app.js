'use strict';

var hubApp = angular.module('hub', ['restangular', 'ngRoute', 'ngResource', 'ui.bootstrap'])
    .config(function($routeProvider, $httpProvider) {
        // Set up our mappings between URLs, templates, and controllers
        $routeProvider
            .when('/', { controller: 'WelcomeController', templateUrl: 'views/welcome.html' })
            .when('/login', { controller: 'LoginController', templateUrl: 'views/login.html' })
            .when('/:username', { controller: 'UserDetailController', templateUrl: 'views/users/user.html' })
            .when('/:username/devices/new', { controller: 'CreateDeviceController', templateUrl: 'views/devices/create.html' })
            .when('/:username/devices/:id', { controller: 'DeviceDetailController', templateUrl: 'views/devices/device.html' })
            .otherwise({ redirectTo: '/' });

        $httpProvider.defaults.headers.common['X-Api-Request-ID'] = guid();
    })
    .constant('USER_ROLES', {
        administrator: 'ADMINISTRATOR',
        user: 'USER'
    });

// Generates a random UUID
function guid() {
    function _p8(s) {
        var p = (Math.random().toString(16) + "000000000").substr(2, 8);
        return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
    }
    return _p8() + _p8(true) + _p8(true) + _p8();
}
