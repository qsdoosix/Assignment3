package ca.carleton.poker.strategy;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.carleton.poker.game.GameOption;
import ca.carleton.poker.game.Player.*;
import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.PokerHand;

/**
 * 
 * 
 *  
 */

@Service
public class AIService {

	 private static final Logger LOG = LoggerFactory.getLogger(AIService.class);

    
    public List<Card> getCardOption(final AIPlayer player, final ArrayList<AIPlayer> players){
    	return  AIStrategy.doStrategy(player, players);
     	
    }

}
