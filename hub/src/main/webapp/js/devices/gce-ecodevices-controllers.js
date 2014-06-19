'use strict';

angular.module('hub.devices.gce-ecodevices.controllers', ['hub.devices.services', 'ngRoute', 'restangular', 'ui.bootstrap'])
    .controller('ConfigureGCEEcoDevicesDeviceController', ['$scope', 'DeviceService', ConfigureGCEEcoDevicesDeviceController]);

function ConfigureGCEEcoDevicesDeviceController($scope, DeviceService) {
    $scope.configuration = {
        port: 80
    };

    $scope.$watch('ecoDeviceForm.$valid', function(val) {
        $scope.$emit('device.configure.form.valid', { value: val, scope: $scope });
    });

}