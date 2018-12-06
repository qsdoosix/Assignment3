package ca.carleton.poker.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.carleton.poker.game.Player.AIPlayer;
import ca.carleton.poker.game.Player.Player;
import ca.carleton.poker.game.Player.RealPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.shuffle;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Handle the ordering of players
 * 
 */
@Service
public class PlayerOrder {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerOrder.class);

    private List<Player> ordering;

    /**
     * Initialize a new round with the given players.
     *
     * @param players the players.
     */
    public void initializeNewRound(final List<Player> players) {

        this.ordering = new ArrayList<>();
 
        // Add ai players first
       for(Player p: players){
        if(p.isAI()) this.ordering.add(p);
       }
        
        // Add real players in reverse order of going
       for(int i =0; i < players.size(); i--){
    	   if(players.get(i).isReal()){
    		   this.ordering.add(players.get(i));
    	   }
       }
      
        LOG.info("New ordering: {}", this.ordering);
    }

    public boolean replaceDisconnectedPlayer(final RealPlayer old, final AIPlayer ai) {
        final int indexOf = this.ordering.indexOf(old);
        if (indexOf == -1) {
            LOG.warn("Warning! Player that disconnect was currently taking his turn - need to force a new result.");
            return false;
        }
        this.ordering.remove(indexOf);

        if (this.ordering.size() == 3) {
            // should always be true - put them before [player 0, ai 1, <INSERT> 2 , DEALER]
            this.ordering.add(2, ai);
        }
        LOG.info("Replaced ordering is {}", this.ordering);
        return true;
    }

  

    public void clearAll() {
        this.ordering = null;
    }
}

