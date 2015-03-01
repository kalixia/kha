'use strict';

hubApp.controller('ConfigureWundergroundDeviceController', function ($scope, DeviceService) {

    $scope.$watch('wundergroundForm.$valid', function(val) {
        $scope.$emit('device.configure.form.valid', { value: val, scope: $scope });
    });

});