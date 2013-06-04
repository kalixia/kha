var gatewayControllers = angular.module('gatewayControllers', ['gatewayServices']);

gatewayControllers.controller('DeviceListController', function ($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
});

gatewayControllers.controller('DeviceDetailController', function ($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
});