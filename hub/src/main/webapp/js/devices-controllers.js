'use strict';

hubApp.controller('CreateDeviceController', function ($scope, DeviceService) {
    $scope.device = {};
    $scope.owner = $scope.user;
    $scope.supportedDevices = DeviceService.findAllSupportedDevices().$object;
    $scope.selectDevice = function(type) {
        $scope.device.type = type;
    };
    $scope.deviceSelected = function(device) {
        if ($scope.device.type == device.type) {
            return true;
        }
    };
    $scope.create = function(owner) {
        return DeviceService.createDevice(owner, $scope.device);
    };
    $scope.$watch('createDeviceForm.$valid', function (val) {
        if ($scope.device.type != null)
            $scope.$emit('device.create.form.valid', { value: val, scope: $scope });
    });
    $scope.$watch('device.type', function() {
        if ($scope.createDeviceForm.$valid)
            $scope.$emit('device.create.form.valid', { value: true, scope: $scope });
    });
});

/*
function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}
*/