// Create a module for our core AMail services
var gatewayServices = angular.module('gateway', []);

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
// Set up our route so the AMail service can find it
gatewayServices.config(gatewayRouteConfig);

// Some fake devices
devices = [
    { id: 0, name: 'Device 1', sensors: [ { value: 12 } ] },
    { id: 0, name: 'Device 2', sensors: [ { value: 17 } ] }
];

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