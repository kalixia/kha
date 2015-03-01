'use strict';

hubApp.controller('ConfigureGCEEcoDevicesDeviceController', function ($scope, DeviceService) {
    $scope.configuration = {
        port: 80
    };

    $scope.$watch('ecoDeviceForm.$valid', function(val) {
        $scope.$emit('device.configure.form.valid', { value: val, scope: $scope });
    });
});