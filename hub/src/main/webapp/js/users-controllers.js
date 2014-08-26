'use strict';

angular.module('hub.users.controllers', ['hub.users.services', 'hub.devices.services', 'hub.security.services'])
    .controller('LoginController', ['$scope', 'SecurityService', '$location', '$log', LoginController])
    .controller('CreateUserController', ['$scope', 'UserService', CreateUserController])
    .controller('UserDetailController', ['$scope', 'DeviceService', 'SecurityService', '$log', UserDetailController]);

function LoginController($scope, SecurityService, $location, $log) {
    $scope.login = function() {
        SecurityService.login($scope.username, $scope.password).then(function() {
            $log.info("Logged in user:");
            var currentUser = SecurityService.getCurrentUser();
            $log.info(currentUser);
            $location.path('/' + currentUser.username);
        });
    }
}

function CreateUserController($scope, UserService, $log) {
    $scope.create = function() {
        return UserService.createUser($scope.user);
    };
    $scope.$watch('createUserForm.$valid', function(val) {
        $scope.$emit('user.create.form.valid', { value: val, scope: $scope });
    });
}

function UserDetailController($scope, DeviceService, SecurityService, $log) {
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
}
