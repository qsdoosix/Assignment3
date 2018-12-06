package ca.carleton.poker.game.Player;

import org.springframework.web.socket.WebSocketSession;

import ca.carleton.poker.game.GameOption;
import ca.carleton.poker.game.entity.card.Hand;

/**
 * Player for web-based poker game
 * 
 *  
 */

public abstract class Player {
	private Hand hand;
	private WebSocketSession session;
	private String uid;
	private GameOption lastOption;
	
		public Player(){}
	   public Player(final WebSocketSession session, String id) {
	        this.session = session;
	        this.hand = new Hand();
	        this.uid = id;
	    }
	   public abstract boolean isReal();
	   public abstract boolean isAI();
	   public String getuid(){
		   return this.uid;
	   }
	    public Hand getHand() {
	        return this.hand;
	    }
	    
	   public WebSocketSession getSession() {
	        return this.session;
	    }

	    public GameOption getLastOption() {
	        return this.lastOption;
	    }

	    public void setLastOption(final GameOption lastOption) {
	        this.lastOption = lastOption;
	    }
	    
	   public void setHand(Hand h){
		   this.hand = h;
	   }

}
