'use strict';

hubApp.controller('LoginController', function ($scope, SecurityService, $location, $log) {
    $scope.login = function() {
        SecurityService.login($scope.username, $scope.password).then(function() {
            $log.info("Logged in user:");
            var currentUser = SecurityService.getCurrentUser();
            $log.info(currentUser);
            $location.path('/' + currentUser.username);
        });
    }
});

hubApp.controller('CreateUserController', function ($scope, UserService, $log) {
    $scope.create = function() {
        return UserService.createUser($scope.user);
    };
    $scope.$watch('createUserForm.$valid', function(val) {
        $scope.$emit('user.create.form.valid', { value: val, scope: $scope });
    });
});

hubApp.controller('UserDetailController', function ($scope, DeviceService, SecurityService, $log) {
    $scope.user = SecurityService.getCurrentUser();
    DeviceService.getUserDevices($scope.user.username).then(function(devices) {
        $log.info("Found devices:");
        $log.info(devices);
        $scope.devices = devices;
    });
    $scope.selectDevice = function(device) {
        $log.debug("Select device " + device.name);
        $scope.selectedDevice = device;
    };
});
