var socket;

if (!window.WebSocket) {
    window.WebSocket = window.MozWebSocket;
}

if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8082/websocket");
    socket.onmessage = function (event) {
        var ta = document.getElementById('responseText');
        ta.value = ta.value + '\n' + event.data;
    };
    socket.onopen = function (event) {
        var ta = document.getElementById('responseText');
        ta.value = "Web Socket opened!";

        socket.send(JSON.stringify({
            path: '/devices',
            method: 'GET'
        }));
    };
    socket.onclose = function (event) {
        var ta = document.getElementById('responseText');
        ta.value = ta.value + "Web Socket closed";
    };
} else {
    alert("Your browser does not support Web Socket.");
}

function send(message) {
    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
        socket.send(JSON.stringify({
            path: '/devices/' + uuid,
            method: 'GET',
            entity: message
        }));
    } else {
        alert("The socket is not open.");
    }
}