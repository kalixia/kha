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

//function gatewayApiUsageConfig($httpProvider) {
//    $httpProvider.default.headers.get['X-Api-Request-ID'] = guid();
//    $httpProvider.default.headers.put('X-Api-Request-ID', guid());
//    console.log("Setting API request headers...");
//}

// Generates a random UUID
//function guid() {
//    function _p8(s) {
//        var p = (Math.random().toString(16) + "000000000").substr(2, 8);
//        return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
//    }
//    return _p8() + _p8(true) + _p8(true) + _p8();
//}

app.config(['$routeProvider', gatewayRouteConfig]);
//app.config(['$httpProvider', gatewayApiUsageConfig]);
//app.config(["$httpProvider", function($httpProvider) {
//  $httpProvider.defaults.headers.common['X-App-Id1'] = 'X';
//  $httpProvider.defaults.headers.get['X-App-Id1'] = 'P';
//}])