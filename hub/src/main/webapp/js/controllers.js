function InstallController($scope, UserService) {
//    UserService.installDone().then(function (value) {
//        $scope.hasUser = value != 0;
//    });
    $scope.hasUser = UserService.installDone();
}

function LoginController($scope) {
}

function CreateUserController($scope, $location, UserService) {
    $scope.user = {};
    $scope.create = function() {
        UserService.createUser($scope.user).then(function() {
            $location.path("/" + $scope.user.username + "/devices/new");
        });
    };
}

//function UserDetailController($scope, $routeParams, User, Device) {
function UserDetailController($scope, $routeParams, Restangular) {
    Restangular.one('', $routeParams.username).get().then(function(user) { $scope.user = user; });
    Restangular.one('', $routeParams.username).getList('devices').then(function(devices) { $scope.devices = devices; });
}

function CreateDeviceController($scope, $routeParams, $location, Restangular) {
    $scope.device = {};
//    $scope.create = function() {
//        Restangular.one('', $sc).post($scope.user).then(function() {
//            $location.path("/" + $scope.user.username + "/devices/new");
//        });
//    };
}
function DeviceListController($scope, Device, DeviceWS) {
    $scope.devices = Device.query();
//    $scope.devices = DeviceWS.getDevices();
}

function DeviceDetailController($scope, $routeParams, Device) {
    $scope.device = Device.get({ id: $routeParams.id })
}