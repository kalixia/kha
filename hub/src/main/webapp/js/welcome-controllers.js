'use strict';

angular.module('hub.welcome.controllers', ['hub.welcome.services', 'hub.security.services'])
    .controller('WelcomeController', ['$scope', 'WelcomeService', 'SecurityService', '$location', '$log', WelcomeController]);

function WelcomeController($scope, WelcomeService, SecurityService, $location, $log) {
    $scope.user = { roles: [ 'ADMINISTRATOR']};
    WelcomeService.installDone().then(function(done) {
        if (done == true)
            $location.path('login');
    });
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
    $scope.currentStepScope = null;
    $scope.validForm = false;

    // beware of 0 index-based array but step numbers 1 index-based.
    $scope.nextStep = function() {
        $scope.errorMessage = '';
        $scope.currentStep = $scope.steps[$scope.currentStep.number];
    };
    $scope.previousStep = function() {
        $scope.currentStep = $scope.steps[$scope.currentStep.number - 2];
    };

    $scope.createUser = function() {
        var user = $scope.currentStepScope.user;
        $log.debug(user);
        WelcomeService.installFor(user).then(function (createdUser) {
            $scope.user = createdUser;
            $scope.nextStep()
        }, function (error) {
            $log.error(error);
        });
    };
    $scope.createDevice = function() {
        $scope.currentStepScope.create(SecurityService.getCurrentUser()).then(function() { $scope.nextStep() }, function(error) {
            $log.error(error);
            switch (error.status) {
                case 400:
                    $scope.errorMessage = error.data;
                    break;
                case 409:
                    $scope.errorMessage = "Device already exists!";
                    break;
            }
        });
    };

    $scope.$on('user.create.form.valid', function(event, data) {
        $scope.validForm = data.value;
        $scope.currentStepScope = data.scope;
    });
    $scope.$on('device.create.form.valid', function(event, data) {
        $scope.validForm = data.value;
        $scope.currentStepScope = data.scope;
    });
}
