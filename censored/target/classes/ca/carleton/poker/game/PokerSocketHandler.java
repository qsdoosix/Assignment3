package ca.carleton.poker.game;

import ca.carleton.poker.game.Player.AIPlayer;
import ca.carleton.poker.game.Player.Player;
import ca.carleton.poker.game.Player.RealPlayer;
import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.HandStatus;
import ca.carleton.poker.game.message.MessageUtil;
import ca.carleton.poker.session.SessionHandler;
import ca.carleton.poker.strategy.AIService;
import org.json.*;
import org.apache.commons.lang3.NotImplementedException;
import org.seleniumhq.jetty7.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ca.carleton.poker.game.message.MessageUtil.Message;
import static ca.carleton.poker.game.message.MessageUtil.message;
import static org.apache.commons.collections.CollectionUtils.size;

/**
 * Socket handler that will contain our Poker controls.
 * 
 *  
 */
@Component
public class PokerSocketHandler extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PokerSocketHandler.class);

    @Autowired
    private PokerGame game;

    @Autowired
    private SessionHandler sessionHandler;
    
    

    /**
     * Whether or not we're accepting connections.
     */
    private boolean acceptingConnections;

    @PostConstruct
    public void init() {
        this.acceptingConnections = true;
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        LOG.info("Opened new session for {}.", session.getId());        
           
       	if (this.acceptingConnections && this.game.registerPlayer(session)) {
            this.sendMessage(session, message(Message.PLAYER_CONNECTED, session.getId()).build());
            this.broadCastMessage(session, message(Message.OTHER_PLAYER_CONNECTED, session.getId()).build());

            if (this.game.getPlayerFor(session).isAdmin()) {
                LOG.info("Sending admin message to player.");
                this.sendMessage(session, message(Message.ADMIN_SET).build());
                // have to wait for the admin to set admin and open the loby 
                this.acceptingConnections = false;
            }

            if (this.game.readyToStart()) {
                this.doReadyToStart();
            }

        } else {
            this.sendMessage(session, message(Message.NOT_ACCEPTING).build());
            this.sessionHandler.registerSessionForDisconnect(session);
            session.close(CloseStatus.NOT_ACCEPTABLE);
            this.game.init();
            this.acceptingConnections =true;
        }
      }
    
    /**
     * Called after a session is closed via session.close()
     *
     * @param session the session.
     * @param status  the close status.
     */
    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        LOG.info("Closing session for {} with status {}.", session.getId(), status);

        if (this.game.getPlayerFor(session) != null) {
            if (this.game.getPlayerFor(session).isAdmin()) {
                this.closeBecauseAdminLeft();
                return;
            }
        }

        if (this.game.deregisterPlayer(session)) {
            if (this.game.isPlaying()) {
                this.broadCastMessage(session, message(Message.OTHER_PLAYER_DISCONNECTED, session.getId()).build());
            }
            LOG.info("Successfully deregistered session {}.", session.getId());
        } else {
            LOG.info("Disabling all accounts because the admin left.");
            this.closeBecauseAdminLeft();
            return;
        }
       

       // Need to deregister any existing AI if we're in a waiting state
        if (this.game.isWaitingForPlayers()) {
            if (this.game.deregisterAI()) {
                LOG.info("Deregistered existing AI.");
            }
        }
      
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception)
            throws Exception {
        LOG.error("TRANSPORT ERROR - Error with the network.", exception);
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message)
            throws Exception {
        LOG.info("Received message from {}: {}.", session.getId(), message.getPayload());
        GameOption option = null;
        Player player = null;
        // KEY_EXTRAVALUE1_EXTRAVALUE2
   
        final String[] contents = message.getPayload().split("\\|");
       
        switch (contents[0]) {
            case "ACCEPT":
                LOG.info("Now accepting connections.");
                this.acceptingConnections = true;
                this.game.openLobby(Integer.parseInt(contents[1]));

                // Case where we're playing with 1 person - need to start right away.
                if (this.game.readyToStart()) {
                
                    this.doReadyToStart();
                	this.game.initiateHands();
                    this.updateCards();
                   LOG.info("Game initiated");
                }
                break;
            case "START_GAME":
                LOG.info("Starting the game.");
                this.broadCastMessageFromServer(message(Message.STARTING_GAME).build());
               // Send each real player their cards.
                this.game.resetRound();
                this.game.initiateHands();
                this.updateCards();
                // AI Plays first
                this.processAI();
                              
                break;
            case "START_RIGGED":
                LOG.info("Starting the rigged game.");
                this.game.resetRound();
                this.broadCastMessageFromServer(message(Message.STARTING_GAME).build());
                this.game.initiateHands();
                this.updateCards();
              //  Thread.sleep(1000);
                Player admin = this.game.getAdmin();
                this.sendMessage(admin.getSession(), message(Message.STARTING_RIGGED_GAME).build());
               
                break;
            case "GAME_STAY":
            	this.game.setWaitingOnReal(false);
                option = GameOption.valueOf(contents[0].split("_")[1]);
                player = this.game.getPlayerFor(session);
                
                this.game.performOption(player, option, null);
                //  LOG.info("Player, option {}, {}", player.getuid(), player.getLastOption() );
                  // Send to other than the player what their move was.
                  this.broadCastMessageFromServer(message(Message.MOVE_MADE, player.getuid(), player.getLastOption()).build());
                  this.updateCards();
                  if(this.game.isResolved()){
                  	this.game.resolveRound();
                  	this.sendResults();
                  	this.resetGame();
                  }else{
                	  this.processPlayers();
                  }
                  break;
            	
            case "GAME_HIT":
            	LOG.info("Player is hitting");
            	player = this.game.getPlayerFor(session);
            	this.game.setWaitingOnReal(false);
                option = GameOption.valueOf(contents[0].split("_")[1]);
                this.sendMessage(session, message(Message.IMPROVE_CARD, player.getuid()).build());
                 LOG.info("Get Card");
                // need to get card;
              
               break;
 
            case "CARD_DONE":
            	LOG.info("Get Card");
            	final String[] nums = contents[1].split("_");
            	int length = nums.length;
            	if(nums.length > 5)length = 5; // There are only 5 cards, something must have been double clicked
            
            	List<Card> cards = new ArrayList<>();      
            	 player = this.game.getPlayerFor(session);
            	 // Get cards; 
            	for(int i = 1; i < nums.length; i++){
            		String[] split = nums[i].split(" ");
            		int id = Integer.parseInt(split[0]);
            		player.getHand().getCards().get(id).setCard(nums[i]); // This is needed for testing
            		if(!cards.contains(player.getHand().getCards().get(id))) 
            				cards.add(player.getHand().getCards().get(id));
            	 }
                 option = GameOption.HIT;

                 this.game.performOption(player, option, cards);
                 // Send to other than the player what their move was
                 this.updateCards(); 
                 this.broadCastMessageFromServer(message(Message.MOVE_MADE, player.getuid(), player.getLastOption()).build());
           
                 if(this.game.isResolved()){
                   	this.sendResults();
                   	this.resetGame();
                   }else{
                 	  this.processPlayers();
                   }
            	 break;
            case "CARDS_UPDATED": 	
            	LOG.info("Getting initial cads of the rigged game");
            	// Users are split between 
            	JSONObject obj = new JSONObject(contents[1]);
            	 // Get cards; 
            	 Iterator keys = obj.keys();
            	 while(keys.hasNext()) {
            	    	String key = (String)keys.next();
            	    	String id = obj.getJSONObject(key).get("id").toString();
            	    	id = id.substring(id.indexOf("(")+1, id.indexOf(")"));
            	    	String[] newCards =  obj.getJSONObject(key).get("hands").toString().split(",");
            	    	player = this.game.getPlayerFor(id);

	            		for(int j = 0; j <5; j++){
	            			player.getHand().getCards().get(j).setCard(newCards[j].trim()); // This is needed for testing
	            		}
	              	 }
            	Thread.sleep(1000);
            	this.updateCards();
            	this.broadCastMessageFromServer(message(Message.RIG_READY).build());
            	Thread.sleep(1000);
            	if(this.game.isResolved()){
                  	this.sendResults();
                  	this.resetGame();
                  }else{
                	  this.rigAiInput();
                  }
            	break;
            	
            	
            case "CARDS_RETURNED":
            	// update cards if changed by selenium
            
            	LOG.info("Get returned cards");
            	// Users are split between 
            	player = null;
	            if (!contents[1].equals("null")){ // checks the player has hit and actually selected a card
	            	obj = new JSONObject(contents[1]);
	            	 // Get cards; 
	            	String id = obj.get("sessionID").toString();
	            	if(id.contains("("))
	            		id = id.substring(id.indexOf("(")+1, id.indexOf(")"));
	            	String[] newCards =  obj.get("hands").toString().split(",");
	            	player = this.game.getPlayerFor(id);
	            	player.setLastOption(GameOption.HIT);
	            	
		            for(int j = 0; j< newCards.length; j++){
			            String[] index = newCards[j].split(":");
			            player.getHand().getCards().get(Integer.parseInt(index[0].trim())).setCard(index[1].trim(), false); // This is needed for testing
		            }	
		           }
	            
	            Thread.sleep(1000);
	            this.updateCards();
	            Thread.sleep(1000);
	            if(this.game.isResolved()){
                  	this.sendResults();
                  	this.resetGame();
                  }else{
                	  this.rigAiInput();
                  }
            	break;
            	
            case "Leaving":
            	break;
            default:
                break;
        }
    }
private void processAI() {
      this.broadCastMessageFromServer(message(Message.PROCESSING_AI).build());
      AIPlayer next = this.game.getNextAI();
      if(next == null){
    	  LOG.info("All AI have done their turn.");
    	  this.broadCastMessageFromServer(message(Message.PROCESSING_Player).build());
    	  this.processPlayers();
      }else{
      //  for(AIPlayer next : this.game.getConnectedAIPlayers()) {
    	  LOG.info("Processing for {}", next.getuid());
    	  next.getHand().setPokerValue();  // Set the poker value to current hand;
    	  this.game.doAITurn(next, false);
          if (next.getLastOption() == GameOption.STAY) {
              LOG.info("Skipping {}'s turn because they STAYED.",  next.getuid());
              this.broadCastMessageFromServer(message(Message.SKIPPING,
            		  next.getuid(),
                      GameOption.STAY).build());
          } else if(next.getLastOption()== GameOption.HIT) {
              this.updateCards();
              this.broadCastMessageFromServer(message(Message.MOVE_MADE,
              		  next.getuid(),
                        GameOption.HIT).build());
        
          }
          processAI();
      }  
}


private void rigAiInput() {
    this.broadCastMessageFromServer(message(Message.PROCESSING_AI).build());
    AIPlayer next = this.game.getNextAI();
    if(next == null){
  	  LOG.info("All AI have done their turn.");
  	  this.broadCastMessageFromServer(message(Message.PROCESSING_Player).build());
  	  this.processPlayers();
    }else{
    //  for(AIPlayer next : this.game.getConnectedAIPlayers()) {
  	  LOG.info("Processing for {}", next.getuid());
  	  next.getHand().setPokerValue();  // Set the poker value to current hand;
  	  this.game.doAITurn(next, true);
        if (next.getLastOption() == GameOption.STAY) {
            LOG.info("Skipping {}'s turn because they STAYED.",  next.getuid());
            this.broadCastMessageFromServer(message(Message.RIG_AI,
          		  next.getuid(),
                   GameOption.STAY).build());
        } else if(next.getLastOption()== GameOption.HIT) {
            Player admin = this.game.getAdmin();
            this.sendMessage(admin.getSession(), message(Message.RIG_AI,
          		  next.getuid(),
                  GameOption.HIT).build());
        }
    }  
}
private void processPlayers() {
	    if(this.game.isResolved()){
         	this.game.resolveRound();
         	this.sendResults();
         	this.resetGame();
	    }else{
	    	 Player p = this.game.getNextPlayer();
	   	     LOG.info("Process turn for {}", p.getuid());
	    	 LOG.info("Sending YOUR_TURN to {}", p.getSession().getId());
	         this.sendMessage(p.getSession(), message(Message.YOUR_TURN).build());
	         this.game.setWaitingOnReal(true);
	         return;
	    }
	  
}
 

 private void closeBecauseAdminLeft() {
        LOG.info("Disabling all accounts because the admin left.");
        this.broadCastMessageFromServer(message(Message.ALL_QUIT).build());
        this.game.getConnectedPlayerSessions()
                .forEach(toClose -> this.sessionHandler.registerSessionForDisconnect(toClose));
        this.game.init();
        this.acceptingConnections = true;
    }

    /**
     * Reset the state of the game.
     */
    private void resetGame() {
        final Player admin = this.game.getAdmin();
        this.game.getConnectedRealPlayers().stream()
                .filter(player -> !player.equals(admin))
                .forEach(player -> this.sendMessage(player.getSession(), message(Message.RESET).build()));
        this.sendMessage(admin.getSession(), message(Message.RESET_ADMIN).build());
        this.game.resetRound();
        this.acceptingConnections = true;
        LOG.info("Reset round - waiting for admin message.");
    }

  
    private void sendResults() {
        // Send cards again but show them all just in case.
    	LOG.info("results");
    	
      	List<Player> order = this.game.resolveRound();
        for (final Player player : this.game.getConnectedPlayers()) {
            this.game.revealCards(player);
        }
        this.updateCards();
        
        
        // Set Hand Values
        for(Player p: this.game.getConnectedRealPlayers()){
        	this.broadCastMessageFromServer(message(Message.PLAYER_VALUE,
                    p.getHand().getPokerValue().toString()).build());
        }
        // SET AI Hand Values
        for(int i = 0; i <this.game.getConnectedAIPlayers().size(); i++){
        	this.broadCastMessageFromServer(message(Message.OTHER_VALUE,
        			i+1,
                    this.game.getConnectedAIPlayers().get(i).getHand().getPokerValue().toString()).build());
        }
       
        
        
        
        // winner is in the last position 
        int r = 1;
        order.get(3).getHand().setHandStatus(HandStatus.WINNER);
        this.broadCastMessageFromServer(message(Message.WINNER,
     		   order.get(3).getuid(),
     		   order.get(3).getHand().getPokerValue(), r).build());
        
     
        for (int i = 2; i >=0; i--) {
        	r++;
        	Player result = order.get(i);
        	   result.getHand().setHandStatus(HandStatus.LOSER);
                    this.broadCastMessageFromServer(message(Message.LOSER,
                    		result.getuid(),result.getHand().getPokerValue(), r).build());                    
        }
        

     
    }

    
    /**
     * Update the cards on the client side.
     */
    
    /**
     * Update the cards on the client side.
     */
    private void updateCards() {
        // Send each real player their cards.
    	LOG.info("Updating cards");
        final Map<Player, List<TextMessage>> cardMessages = this.game.buildHandMessages();
        cardMessages.forEach((player, messages) ->
                messages.forEach(toSend -> this.sendMessage(player.getSession(), toSend)));
    }

  

    /**
     * When we're ready to start - register the AI and send the messages.
     */
    private void doReadyToStart() {
        this.game.registerAIPlayer();
        this.acceptingConnections = false;
        LOG.info("Game is now ready to start - sending message!");
        final Player admin = this.game.getAdmin();
        this.sendMessage(admin.getSession(), message(Message.READY_TO_START).build());
        this.broadCastMessage(admin.getSession(),
                message(Message.OTHER_READY_TO_START, admin.getSession().getId()).build());
    }

    /**
     * Send a message to the given session.
     *
     * @param recipient the session.
     * @param message   the message.
     */
    private void sendMessage(final WebSocketSession recipient, final TextMessage message) {
        try {
            recipient.sendMessage(message);
        } catch (final IOException exception) {
            LOG.error("Error sending a message.", exception);
            this.closeSession(recipient, CloseStatus.PROTOCOL_ERROR);
        }
    }

    /**
     * Broadcast a message to the other users connected to this socket.
     *
     * @param sender  the sender.
     * @param message the message.
     */
    private void broadCastMessage(final WebSocketSession sender, final TextMessage message) {
        LOG.trace("SENDING {} TO {}.", message.getPayload(), this.game.getConnectedPlayerSessions());
        this.game.getConnectedRealPlayers().stream()
                .map(Player::getSession)
                .filter(session -> !session.getId().equals(sender.getId()))
                .forEach(session ->
                {
                    try {
                        session.sendMessage(message);
                    } catch (final Exception exception) {
                        this.closeSession(session, CloseStatus.PROTOCOL_ERROR);
                    }
                });
    }

    /**
     * Send a message to every real player connected.
     *
     * @param message the message.
     */
    private void broadCastMessageFromServer(final TextMessage message) {
        this.game.getConnectedRealPlayers().stream()
                .map(Player::getSession)
                .forEach(session ->
                {
                    try {
                        session.sendMessage(message);
                    } catch (final Exception exception) {
                        this.closeSession(session, CloseStatus.PROTOCOL_ERROR);
                    }
                });
    }

    /**
     * Close a session.
     *
     * @param session the session.
     * @param status  the reason why we're closing.
     */
    private void closeSession(final WebSocketSession session, final CloseStatus status) {
        try {
            session.close(status);
        } catch (final IOException exception) {
            LOG.error("Exception when trying to close session!", exception);
        }
    }

}
