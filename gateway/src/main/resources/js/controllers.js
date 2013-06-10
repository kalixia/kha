function UserDetailController($scope, $routeParams, User, Device) {
    $scope.user = User.get({ username: $routeParams.username });
    $scope.devices = Device.get({ username: $routeParams.username });
}

function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}