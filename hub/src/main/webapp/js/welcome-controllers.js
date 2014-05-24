'use strict';

function WelcomeController($scope, UserService, $log) {
    $scope.user = {};
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
        $scope.currentStepScope.create().then(function() { $scope.nextStep() }, function(error) {
            $log.error(error);
            switch (error.status) {
                case 409:
                    $scope.errorMessage = "User already exists!";
                    break;
            }
        });
    };
    $scope.createDevice = function() {
        $scope.currentStepScope.create().then(function() { $scope.nextStep() }, function(error) {
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
