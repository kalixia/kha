var app = angular.module('gateway', ['gatewayServices']);

// Set up our mappings between URLs, templates, and controllers
function gatewayRouteConfig($routeProvider) {
    $routeProvider.
//        when('/', {
//            controller: DeviceListController,
//            templateUrl: 'devices.html'
//        }).
        when('/:username', {
            controller: UserDetailController,
            templateUrl: 'user.html'
        }).
        when('/:username/device/:id', {
            controller: DeviceDetailController,
            templateUrl: 'device.html'
        }).
        otherwise({
            redirectTo: '/'
        });
}

// Generates a random UUID
function guid() {
    function _p8(s) {
        var p = (Math.random().toString(16) + "000000000").substr(2, 8);
        return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
    }
    return _p8() + _p8(true) + _p8(true) + _p8();
}

app.config(function($routeProvider, $httpProvider) {
    gatewayRouteConfig($routeProvider);
    $httpProvider.defaults.headers.common['X-Api-Request-ID'] = guid();
    console.log($httpProvider.defaults.headers);
});