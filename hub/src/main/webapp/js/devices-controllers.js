'use strict';

angular.module('hub.devices.controllers', ['hub.devices.services', 'ngRoute', 'restangular', 'ui.bootstrap'])
    .controller('CreateDeviceController', ['$scope', 'DeviceService', CreateDeviceController]);
//    .controller('CreateDeviceController', ['$scope', 'DeviceService', DeviceListController])
//    .controller('CreateDeviceController', ['$scope', 'DeviceService', DeviceDetailController]);

function CreateDeviceController($scope, DeviceService) {
    $scope.device = {};
    $scope.owner = $scope.user;
    $scope.create = function(owner) {
        return DeviceService.createDevice(owner, $scope.device);
    };
    $scope.$watch('createDeviceForm.$valid', function (val) {
        $scope.$emit('device.create.form.valid', { value: val, scope: $scope });
    });
}

/*
function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}
*/