var ws;

function connect() {
    var username = document.getElementById("username").value;

    var host = document.location.host;
    var pathname = document.location.pathname;
    console.log('creating websocket object');

    ws = new WebSocket("wss://localhost:8080/chat/" + username);
    console.log('connected to websocket!!')


    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        log.innerHTML += message.from + " : " + message.content + "\n";
    };
}

function send() {
    var content = document.getElementById("msg").value;
    var json = JSON.stringify({
        "content":content
    });
    console.log("sending data to websocket ", json)

    ws.send(json);
}
