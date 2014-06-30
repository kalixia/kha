'use strict';

angular.module('hub.devices.wunderground.controllers', ['hub.devices.services', 'ngRoute', 'restangular', 'ui.bootstrap'])
    .controller('ConfigureWundergroundDeviceController', ['$scope', 'DeviceService', ConfigureWundergroundDeviceController]);

function ConfigureWundergroundDeviceController($scope, DeviceService) {
    $scope.configuration = {};

    $scope.$watch('wundergroundForm.$valid', function(val) {
        $scope.$emit('device.configure.form.valid', { value: val, scope: $scope });
    });

    /*
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
        $scope.$emit('device.create.form.valid', { value: val, scope: $scope });
    });
    */
}