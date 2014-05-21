function WelcomeController($scope, UserService) {
    $scope.hasUser = UserService.installDone();
    $scope.steps = [
        {
            number: 1,
            description: 'Welcome to Kha',
            complete: false,
            disabled: false
        },
        {
            number: 2,
            description: 'Setup your account for secured access',
            complete: false,
            disabled: true
        },
        {
            number: 3,
            description: 'Add your first device or sensor',
            complete: false,
            disabled: true
        }
    ];
    $scope.currentStep = $scope.steps[0];
    $scope.validForm = false;

    // beware of 0 index-based array but step numbers 1 index-based.
    $scope.nextStep = function() {
        $scope.currentStep = $scope.steps[$scope.currentStep.number];
    };
    $scope.previousStep = function() {
        $scope.currentStep = $scope.steps[$scope.currentStep.number - 2];
    };

    $scope.createUser = function() {
        $scope.create();
        $scope.nextStep();
    };

    $scope.$on('user.create.form.valid', function(event, valid) {
        $scope.validForm = valid;
    });
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
    $scope.$watch('createUserForm.$valid', function(val) {
        $scope.$emit('user.create.form.valid', val);
    });
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