'use strict';

angular.module('hub.devices.wunderground.controllers', ['hub.devices.services', 'ngRoute', 'restangular', 'ui.bootstrap'])
    .controller('ConfigureWundergroundDeviceController', ['$scope', 'DeviceService', ConfigureWundergroundDeviceController]);

function ConfigureWundergroundDeviceController($scope, DeviceService) {

    $scope.$watch('wundergroundForm.$valid', function(val) {
        $scope.$emit('device.configure.form.valid', { value: val, scope: $scope });
    });

}