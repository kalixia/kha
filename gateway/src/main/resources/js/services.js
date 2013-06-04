var gatewayServices = angular.module('gateway', ['ngResource']);

gatewayServices.factory('Device', ['$resource', function($resource) {
    return $resource('http://localhost:8082/devices/:id', {id: '@id'});
}]);

//services.factory('MultiRecipeLoader', ['Recipe', '$q', function (Recipe, $q) {
//    return function () {
//        var delay = $q.defer();
//        Recipe.query(function (recipes) {
//            delay.resolve(recipes);
//        }, function () {
//            delay.reject('Unable to fetch recipes');
//        });
//        return delay.promise;
//    };
//}]);