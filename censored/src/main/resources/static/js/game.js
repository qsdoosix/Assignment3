var ws = null;
var playerId = null;
var cards = "";
var rigged = false;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
}

function setGameOptionsEnabled(enabled) {
    document.getElementById('stay').disabled = !enabled;
    document.getElementById('hit').disabled = !enabled;
    document.getElementById('done').disabled = true;
}

function setAdmin(enabled) {
    admin = true;
    document.getElementById('open').disabled = !enabled;
    document.getElementById('shutdown').disabled = !enabled;
    document.getElementById('numberPlayers').disabled = !enabled;
}

function enableStart(enabled) {
    document.getElementById('start').disabled = !enabled;
    document.getElementById('rig').disabled =!enabled;
}

function setUID(uid) {
    if (uid != null) {
        var stripped = uid.replace(/\./g, ' ');
        document.getElementById('consoleText').innerHTML = 'Console (UID: ' + stripped + ')';
        playerId = uid;
    } else {
        document.getElementById('consoleText').innerHTML = 'Console';
        playerId = '';
    }
}
/**
 * Connect to the server.
 */
function connect() {
    // hardcoded endpoint, oh no!
    ws = new SockJS('/game');
    ws.onopen = function () {
        setConnected(true);
        clientLog('Connection opened.');
    };
    ws.onmessage = function (event) {
        dispatch(event.data);
    };
    ws.onclose = function () {
        setUID();
        clientLog('Connection closed.');
        disconnect();
    };
}

/**
 * Disconnect from the server.
 */
function disconnect() {

    // Before we disconnect let them know we're leaving if its our turn
    if (document.getElementById('stay').disabled === false) {
        ws.send('LEAVING');
    }

    if (ws != null) {
        ws.close();
        ws = null;
    }
    setConnected(false);
    setGameOptionsEnabled(false);
    resetYourText();
    resetOtherText();
    setAdmin(false);
    enableStart(false);
    removeCards();
}

/**
 * Determine what to do with the message.
 *
 * @param message the message.
 */
function dispatch(message) {
    // split message into three: [SENDER, KEY, PAYLOAD]
    var split = message.split('|');
    var logMessage = split[0].concat(split[2]);
    console.log(split);
    switch (split[1]) {
        case 'OTHER+DISCONNECTED':
        case 'OTHER+READY+TO+START':
        case 'OTHER+CONNECTED':
        case 'CONNECTED':
            log(logMessage);
            var connectedMessage = logMessage.split(' ');
            var last = connectedMessage[connectedMessage.length - 1];
            if (split[1] === 'CONNECTED') {
                setUID(last);
            }
            break;
        case 'NOT+ACCEPTING':
            log(logMessage);
            //disconnect(); for now done by the server...this is the work around
            break;
        case 'ADMIN':
            log(logMessage);
            setAdmin(true);
            enableStart(false);
            break;
        case 'GAME+START':
            log(logMessage);
            break;
        case 'RIGGED+GAME':
            enableStart(false);
            removeCards();
            rig_game()
            log(logMessage);
            break;
        case 'RIG+READY':
            rig_ready();
            log(logMessage);
            break;
        case 'IMPROVE+CARD':
            log(logMessage);
            getCard();
            break;
        case 'OTHER+MOVE':
            log(logMessage);
            break;
                  break;
        case 'RIG+AI':
            returnCards(split[2], split[3]);
            break;
        case 'SKIP':
            log(logMessage);
            break;
        case 'ADD+PLAYER+CARD':
            addCardForPlayer(split[2],split[3]);
            break;
        case 'ADD+OTHER+PLAYER+CARD':
            addCardForOther(split[2], split[3], split[4]);
            break;
        case 'PLAYER+VALUE':
            updatePlayerValue(split[2]);
            break;
        case 'OTHER+VALUE':
            updateOtherValue(split[2], split[3]);
            break;
        case 'READY+TO+START':
            log(logMessage);
            enableStart(true);
            break;
        case 'DEALING+CARDS':
            removeCards();
            log(logMessage);
            break;
        case 'YOUR+TURN':
            setGameOptionsEnabled(true);
            log(logMessage);
            break; 
        case 'YOUR+TURN+RIG':
            setGameOptionsEnabled(true);
            returnCards(split[2], split[3]);
            break;   
        case 'AI+TURN':
            log(logMessage);
            break;
        case 'WINNER':
            log(logMessage);
            break;
        case 'LOSER':
            log(logMessage);
            break;
        case 'RESET':
            log(logMessage);
            setGameOptionsEnabled(false);
            break;
        case 'RESET+ADMIN':
            log(logMessage);
            setGameOptionsEnabled(false);
            enableStart(true);
            break;
        case 'ALL+QUIT':
            log(logMessage);
            setGameOptionsEnabled(false);
            setAdmin(false);
            enableStart(false);
            break;
        default:
            console.log('Unknown message received');
            break;
    }
}


function start_rigged(){
    rigged = true;

    ws.send('START_RIGGED|');
    clientLog("starting Rigged Game");
}

function rig_ready(){
        var e = document.createElement("div");
    e.innerHTML = "RIGGED";
    rigged = true;
    e.id = "rigged";
    document.getElementById('connect-container').appendChild(e);
}
function rig_game(){
   var jsonCards = {}
   if(admin){
       clientLog("Setting all cards");
       // get player ranks
        var hands = prompt("This prompt allows you to rig cards\n"
            +"Set rank using its rank-?:  ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit using, hearts, diams, clubs, or spades\n\n"
            +"Please enter 5 cards for Player: ");
        var cards =  hands.split(",");
        var id =  document.getElementById("yourHandText").innerHTML.match(/\(.+?\)/g);
        
        if (cards.length < 5 ) {
            txt = "Invalid cards";
        } else{
            jsonCards.player ={id, hands};
        }

        // get user 1
        hands = prompt("This prompt allows you to rig cards\n"
            +"Set rank using its rank-?: ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit using, hearts, diams, clubs, or spades\n\n"
            +"Please enter 5 cards for other Hand 1");
        cards =  hands.split(",");
        id =  document.getElementById("otherHandText1").innerHTML.match(/\(.+?\)/g);
        if (cards.length < 5 ) {
           alert("Invalid cards");
             return;
        } else{
            jsonCards.other1 = {id, hands};
        }


        // get user 2
        hands = prompt("This prompt allows you to rig cards\n"
            +"Set rank using its rank-?: ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit using, hearts, diams, clubs, or spades\n\n"
            +"Please enter 5 cards for other Hand 2");
        cards =  hands.split(",");
        id =  document.getElementById("otherHandText2").innerHTML.match(/\(.+?\)/g);
      
        if (cards.length < 5 ) {
             alert("Invalid cards");
             return;
        } else{
            jsonCards.other2 = {id, hands};
        }


        // get user 3 
        var hands = prompt("This prompt allows you to rig cards\n"
            +"Set rank using its rank-?: ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit using, hearts, diams, clubs, or spades\n\n"
            +"Please enter 5 cards for other Hand 3");
        var cards =  hands.split(",");
        var id =  document.getElementById("otherHandText3").innerHTML.match(/\(.+?\)/g);
        if (cards.length < 5 ) {
           alert("Invalid cards");
             return;
        } else{
            jsonCards.other3 = {id, hands};
        }


        // send message
        ws.send('CARDS_UPDATED|'.concat(JSON.stringify(jsonCards)));
     }
    
}

function returnCards(sessionID, move){
   clientLog(sessionID+ "choose to "+ move);
    var jsonCards = null;
    if(move == "HIT"){
        // get Improved cards
        var hands = prompt("This prompt allows you to rig cards\n"
            +"set card number ( 0->5)"
            +"Set rank: using its rank-?:  where ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit: using, hearts, diams, clubs, or spades\n"
            +"you must enter combinations separated by commas\n\n"
            +"please enter for "+ sessionID + " (id:rank suit): ");
        var cards =  hands.split(",");
        jsonCards = {sessionID, hands};
    }
        ws.send('CARDS_RETURNED|'.concat(JSON.stringify(jsonCards)));
        clientLog("Improving cards: " + JSON.stringify(jsonCards));  
     
}


/**
 * Send option chosen back.
 */
function game_option(option) {
    ws.send('GAME_'.concat(option));
    document.getElementById('done').disabled =true;
    console.log('Sent ' + option);
    clientLog('You decided to ' + option + '. Sending to server ');
    setGameOptionsEnabled(false);
}

/*
*  get user to pick a card 
 
*/
function getCard(){
   var done = document.getElementById('done').disabled = false;
}


/*
* Send cards back to server
*/
function sendCards(){
        window.focus();
        var eles = document.forms["playerHandCards"].getElementsByTagName("input");
        document.getElementById('done').disabled = true; 
        var cards = " ";
        var sessionID =  document.getElementById("yourHandText").innerHTML.match(/\(.+?\)/g);

        if(rigged){
            var hands= prompt("This prompt allows you to rig cards\n"
            +"set card number ( 0->5)"
            +"Set rank: using its rank-?:  where ? is numerical number or, a, j,q,k (ace, jack, queen, king)\n"
            +"Set suit: using, hearts, diams, clubs, or spades\n"
            +"you must enter combinations separated by commas\n\n"
            +"please enter for "+ sessionID+ " (id:rank suit): ");
            jsonCards = {sessionID, hands};
            ws.send('CARDS_RETURNED|'.concat(JSON.stringify(jsonCards)));
            clientLog("Improving cards: " + JSON.stringify(jsonCards));  
        }
        else{
            for(var i = 0; i< eles.length; i++){
                 if(eles[i].checked == true){
                    clientLog("selected "+ eles[i].value.replace("card ", "_"));
                 cards += eles[i].value.replace("card ", "_"+i+" ");
                 }
    
            }
         ws.send('CARD_DONE|'.concat(cards));
        clientLog('You decided to improve card ' + cards + '. Sending to server - please wait for results.');
        cards ="";
        }
        
    }           

/**
 * Send the start message.
 */
function start() {
    ws.send('START_GAME');
    enableStart(false);
    removeCards();
    var r =     document.getElementById('rigged');
    document.getElementById("connect-container").removeChild(r);

}

/**
 * Add a new card to the player's list.

*/

function addCardForPlayer(card, sessionID) {
    var id = document.getElementById('playerHandCards').childElementCount+1;
    var input = document.createElement('div');
    input.innerHTML = card;
    input.id  = ("PlayerCard"+id);
    document.getElementById('yourHandText').innerHTML = "Your Hand (" + sessionID + ")";
    document.getElementById('playerHandCards').appendChild(input);
}


/**
 * Add a new card for another player.
   */
function addCardForOther(card, id, sessionID) {
    var input = document.createElement('div');
    input.innerHTML = card;
    input.id=("otherHand"+id+ "Card" + document.getElementById('otherHandCards'.concat(id)).childElementCount);
    console.log('Trying to append to ' + 'otherHandCards'.concat(id));
    document.getElementById('otherHandCards'.concat(id)).appendChild(input);
    document.getElementById('otherHandText'.concat(id)).innerHTML = "Other Player's Hand (" + sessionID + ")";

    
}

function removeOldValue(old) {
    var split = old.split('~');
    return split[0];
}

function updatePlayerValue(value) {
    var old = removeOldValue(document.getElementById('yourHandText').innerHTML);
    document.getElementById('yourHandText').innerHTML = old.concat(" ~ Value: ".concat(value));
}

function updateOtherValue(index, value) {
    var old = removeOldValue(document.getElementById('otherHandText'.concat(index)).innerHTML);
    document.getElementById('otherHandText'.concat(index)).innerHTML = old.concat(" ~ Value: ".concat(value));
}

function resetYourText() {
    document.getElementById('yourHandText').innerHTML = "Your Hand";
}


function resetOtherText() {
    document.getElementById('otherHandText1').innerHTML = "Other Player's Hand";
    document.getElementById('otherHandText2').innerHTML = "Other Player's Hand";
    document.getElementById('otherHandText3').innerHTML = "Other Player's Hand";
}

/**
 * Remove all cards.
 */
function removeCards() {
    console.log('Emptied cards.');
    document.getElementById('playerHandCards').innerHTML = "";
    document.getElementById('otherHandCards1').innerHTML = "";
    document.getElementById('otherHandCards2').innerHTML = "";
    document.getElementById('otherHandCards3').innerHTML = "";
}

/**
 * Open connections for other players.
 */
function acceptOthers() {
    if (ws != null) {
        var numP = document.getElementById('numberPlayers').value;

        clientLog('Opening the lobby with specified settings. When the correct number of players have connected, the start button will become available.');
        var send = 'ACCEPT|' + numP;
        ws.send(send);
        document.getElementById('open').disabled = true;
        document.getElementById('numberPlayers').disabled = true;
    } else {
        alert('Connection not established, please connect.');
    }
}

/**
 * Log from the client.
 * @param message the message.
 */
function clientLog(message) {
    var pad = '00';
    var date = new Date();
    var hour = "" + date.getHours();
    var hourPad = pad.substring(0, pad.length - hour.length) + hour;
    var min = "" + date.getMinutes();
    var minPad = pad.substring(0, pad.length - min.length) + min;
    var hourMin = hourPad + ':' + minPad;
    var prefix = '<strong>' + hourMin + ' Client' + '</strong>: ';
    log(prefix + message);
}

/**
 * Log to the console
 * @param message the message.
 */
function log(message) {
    var console = document.getElementById('console');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.innerHTML = message;

    console.appendChild(p);
    while (console.childNodes.length > 25) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
}

/**
 * Shutdown the spring boot server.
 */
function shutdown() {
    clientLog('Sent shutdown command.');
    $.post(
        "http://localhost:8080/shutdown",
        null,
        function () {
            alert("Website shutting down.");
        }
    );
}
