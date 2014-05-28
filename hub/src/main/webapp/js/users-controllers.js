'use strict';

angular.module('hub.users.controllers', ['hub.users.services', 'hub.devices.services', 'ngRoute'])
    .controller('LoginController', ['$scope', LoginController])
    .controller('CreateUserController', ['$scope', 'UserService', CreateUserController])
    .controller('UserDetailController', ['$scope', '$routeParams', 'UserService', 'DeviceService', UserDetailController]);

function LoginController($scope) {
}

function CreateUserController($scope, UserService) {
    $scope.create = function() {
        return UserService.createUser($scope.user);
    };
    $scope.$watch('createUserForm.$valid', function(val) {
        $scope.$emit('user.create.form.valid', { value: val, scope: $scope });
    });
}

function UserDetailController($scope, $routeParams, UserService, DeviceService) {
    UserService.getUser($routeParams.username).then(function(user) {
        $scope.user = user;
    });
    DeviceService.getUserDevices($routeParams.username).then(function(devices) {
        $scope.devices = devices;
    })
}
