package ca.carleton.poker.game.Player;

import ca.carleton.poker.game.GameOption;
import ca.carleton.poker.game.entity.card.Hand;

import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a player.
 * 
 */
public class RealPlayer extends Player {
	
	private boolean isAdmin;

    public RealPlayer(final WebSocketSession session, String id) {
        super(session, id);
        isAdmin =false;
    }
    @Override
    public boolean isReal() {
        return true;
    }

	@Override
	public boolean isAI() {
		return false;
	}
	
	public boolean isAdmin() {
		return this.isAdmin;
	}

	    public void setAdmin(final boolean isAdmin) {
	        this.isAdmin = isAdmin;
	    }



}
