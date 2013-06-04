// Create a module for our core AMail services
var gatewayServices = angular.module('gateway');

// Set up our mappings between URLs, templates, and controllers
function gatewayRouteConfig($routeProvider) {
    console.log('ici');
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
    $httpProvider.default.headers.get['X-Api-Request-ID'] = guid();
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

// Set up our route so the AMail service can find it
gatewayServices.config(function($routeProvider, $httpProvider) {
    gatewayRouteConfig($routeProvider);
//    gatewayApiUsageConfig($httpProvider);
});

// Some fake devices
var devices;

function DeviceListController($scope, $http) {
    $http.get('http://localhost:8082/devices')
        .success(function (data, status, headers, config) {
            $scope.devices = data;
        })
        .error(function (data, status, headers, config) {
            console.log("Error while fetching device. Status: " + status);
        });
}

function DeviceDetailController($scope, $http, $routeParams) {
    $http.get('http://localhost:8082/devices/' + $routeParams.id)
        .success(function (data, status, headers, config) {
            $scope.device = data;
        })
        .error(function (data, status, headers, config) {
            console.log("Error while fetching device. Status: " + status);
        });
}