'use strict';

var devicesServices = angular.module('hub.devices.services', ['hub.security.services', 'restangular']);

devicesServices.factory('DeviceService', ['Restangular', 'SecurityService', '$log',
    function deviceServiceFactory(Restangular, SecurityService, $log) {
        return {
            getUserDevices: function(username) {
                return Restangular.one('', username).getList('devices');
            },
            createDevice: function(owner, device) {
                return Restangular.one('', owner.username).all('devices').post(device);
            },
            configureDevice: function(owner, device, configuration) {
                return Restangular
                    .one('', owner.username)
                    .one('devices', device)
                    .one('', 'configuration')
                    .customPUT(configuration);
            },
            findAllSupportedDevices: function() {
                return Restangular
                    .one(SecurityService.getCurrentUser().username, 'devices')
                    .getList('supported');
            }
        }
    }
]);

//hubServices.factory('User', function($resource) {
//    return $resource('http://localhost\\:8082/:username', {username: '@id'});
//});

//hubServices.factory('Device', function($resource) {
//    return $resource('http://localhost\\:8082/:username/devices/:id', {username:'', name: '@id'});
//});

/*
hubServices.factory('DeviceWS', ['$q', function ($q) {
    // We return this object to anything injecting our service
    var Service = {};
    // Keep all pending requests here until they get responses
    var callbacks = {};
    // Create a unique callback ID to map requests to responses
    var currentCallbackId = 0;

    // Create our websocket object with the address to the websocket
    var ws = new WebSocket("ws://localhost:8082/websocket");
    ws.onopen = function () {
        console.log("Socket has been opened!");
    };
    ws.onmessage = function (message) {
        listener(message);
    };

    function sendRequest(request) {
        var defer = $q.defer();
        var callbackId = getCallbackId();
        callbacks[callbackId] = {
            time: new Date(),
            cb: defer
        };
        request.callback_id = callbackId;
        console.log('Sending request', request);
        ws.send(JSON.stringify(request));
        return defer.promise;
    }

    function listener(data) {
        var messageObj = data;
        console.log("Received data from websocket: ", messageObj);
        // If an object exists with callback_id in our callbacks object, resolve it
        if (callbacks.hasOwnProperty(messageObj.callback_id)) {
            console.log(callbacks[messageObj.callback_id]);
            $rootScope.$apply(callbacks[messageObj.callback_id].cb.resolve(messageObj.data));
            delete callbacks[messageObj.callbackID];
        }
    }

    // This creates a new callback ID for a request
    function getCallbackId() {
        currentCallbackId += 1;
        if (currentCallbackId > 10000) {
            currentCallbackId = 0;
        }
        return currentCallbackId;
    }

    // Define a "getter" for getting customer
    Service.getDevices = function () {
        var request = {
            path: '/devices'
        }
        // Storing in a variable for clarity on what sendRequest returns
        var promise = sendRequest(request);
        return promise;
    }

    return Service;
}]);
*/