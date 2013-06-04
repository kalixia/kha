var gatewayServices = angular.module('gateway', ['gatewayServices']);

// Set up our mappings between URLs, templates, and controllers
function gatewayRouteConfig($routeProvider) {
    $routeProvider.
        when('/', {
            controller: DeviceListController,
            templateUrl: 'devices.html'
        }).
        // Notice that for the detail view, we specify a parameterized URL component
        // by placing a colon in front of the id
        when('/device/:id', {
            controller: DeviceDetailController,
            templateUrl: 'device.html'
        }).
        otherwise({
            redirectTo: '/'
        });
}

function gatewayApiUsageConfig($httpProvider) {
//    $httpProvider.default.headers.get['X-Api-Request-ID'] = guid();
    $httpProvider.default.headers.put('X-Api-Request-ID', guid());
    console.log("Setting API request headers...");
}

// Generates a random UUID
function guid() {
    function _p8(s) {
        var p = (Math.random().toString(16) + "000000000").substr(2, 8);
        return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
    }
    return _p8() + _p8(true) + _p8(true) + _p8();
}

gatewayServices.config(['$routeProvider', gatewayRouteConfig]);
//gatewayServices.config(['$httpProvider', gatewayApiUsageConfig]);