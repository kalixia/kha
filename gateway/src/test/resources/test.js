var socket;

if (!window.WebSocket) {
    window.WebSocket = window.MozWebSocket;
}

if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8081/websocket");
    socket.onmessage = function (event) {
        var ta = document.getElementById('responseText');
        ta.value = ta.value + '\n' + event.data
    };
    socket.onopen = function (event) {
        var ta = document.getElementById('responseText');
        ta.value = "Web Socket opened!";
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
        socket.send(JSON.stringify({
            path: '/',
            method: 'POST',
            entity: message
        }));
    } else {
        alert("The socket is not open.");
    }
}