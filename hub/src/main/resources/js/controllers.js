//function UserDetailController($scope, $routeParams, User, Device) {
function UserDetailController($scope, $routeParams, Restangular) {
    $scope.user = Restangular.one('', $routeParams.username).get();
    $scope.devices = Restangular.one('', $routeParams.username).getList('devices')
}

function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}