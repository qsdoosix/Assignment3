package ca.carleton.poker.game.Player;

import org.springframework.web.socket.WebSocketSession;

/**
 * An AI player.
 * 
 */
public class AIPlayer extends Player {

    public AIPlayer(final WebSocketSession session, String id) {
        super(session, id);
    }

    @Override
    public boolean isAI() {
        return true;
    }
	@Override
	public boolean isReal() {
		return false;
	}



}
