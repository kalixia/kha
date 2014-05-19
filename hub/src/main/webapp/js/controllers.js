function WelcomeController($scope, Restangular) {
    Restangular.one('admin/users', 'count').get().then(function(value) {
        $scope.hasUser = value != 0;
    })
}

function LoginController($scope) {
}

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