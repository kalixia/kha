'use strict';

function LoginController($scope) {
}

function CreateUserController($scope, $location, UserService) {
    $scope.create = function() {
        return UserService.createUser($scope.user);
    };
    $scope.$watch('createUserForm.$valid', function(val) {
        $scope.$emit('user.create.form.valid', { value: val, scope: $scope });
    });
}

//function UserDetailController($scope, $routeParams, User, Device) {
function UserDetailController($scope, $routeParams, Restangular) {
    Restangular.one('', $routeParams.username).get().then(function(user) { $scope.user = user; });
    Restangular.one('', $routeParams.username).getList('devices').then(function(devices) { $scope.devices = devices; });
}
