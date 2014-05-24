'use strict';

var hubServices = angular.module('hubServices', ['ngResource', 'restangular'])
    .config(function(RestangularProvider) {
        RestangularProvider.setBaseUrl("http://localhost:8082");
    });
