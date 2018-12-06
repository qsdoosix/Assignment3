package ca.carleton.poker.strategy;

import java.util.ArrayList;
import java.util.List;

import ca.carleton.poker.game.Player.*;
import ca.carleton.poker.game.entity.card.Card;

/**
 * 
 * shows strategies are abstracted
 */

public class AIStrategyContext {
	 AIStrategy strategy;

	   public AIStrategyContext(AIStrategy strategy){
	      this.strategy = strategy;
	   }

	   public static List<Card> executeStrategy(AIPlayer p, ArrayList<AIPlayer> players){
	      return AIStrategy.doStrategy(p, players);
	   }
}
