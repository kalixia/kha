// Some fake devices
var devices;

function DeviceListController($scope, Device) {
    $scope.devices = Device.query();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}

gatewayServices.controller(DeviceListController);
gatewayServices.controller(DeviceDetailController);