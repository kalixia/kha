function WelcomeController($scope, Restangular) {
    Restangular.one('admin/users', 'count').get().then(function(value) {
        $scope.hasUser = value != 0;
    });
}

function LoginController($scope) {
}

function CreateUserController($scope, $location, Restangular) {
    $scope.user = {};
    $scope.create = function() {
        Restangular.all('').post($scope.user).then(function() {
            $location.path("/" + $scope.user.username);
        });
    };
}

//function UserDetailController($scope, $routeParams, User, Device) {
function UserDetailController($scope, $routeParams, Restangular) {
    Restangular.one('', $routeParams.username).get().then(function(user) { $scope.user = user; });
    Restangular.one('', $routeParams.username).getList('devices').then(function(devices) { $scope.devices = devices; });
}

function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}