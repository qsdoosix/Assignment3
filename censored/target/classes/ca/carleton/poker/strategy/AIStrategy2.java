package ca.carleton.poker.strategy;

import java.util.ArrayList;
import java.util.List;

import ca.carleton.poker.game.GameOption;
import ca.carleton.poker.game.Player.*;
import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.PokerHand;

/**
 * 
 * - strategy 2
 */

public class AIStrategy2 implements AIStrategy{

	AIPlayer player; 
	ArrayList<AIPlayer> others;
	
	public AIStrategy2(AIPlayer p, ArrayList<AIPlayer> others){
		this.player = p;
		this.others = others;
		
	}
	
	
	// keep pairs an exchange all others 
	public List<Card> chooseCard(){
	    	List<Card> improve = new ArrayList<>();
	        List<Card> cards = this.player.getHand().sortRank();
	        PokerHand handvalue = this.player.getHand().getPokerValue();
	        
	        
	        // check if cards are a pair 
	        if(handvalue.equals(PokerHand.TWO_PAIR)){
	        	LOG.info("AI 2 ");
	        	// {a,a,b,b,x}
	        	if(cards.get(0).getRank() == cards.get(1).getRank() &&
	        		cards.get(2).getRank() == cards.get(3).getRank()){
	        		improve.add(cards.get(4));
	        	}
	        	//{x,a,a,b,b}
	        	else{
	        		improve.add(cards.get(0));
	        	}
	        }else if(handvalue.equals(PokerHand.ONE_PAIR)){
	        	LOG.info("AI 1");
	        	//{a,a,x,y,z}
	        	if(cards.get(0).getRank() == cards.get(1).getRank()){
	        		improve.add(cards.get(2));
	        		improve.add(cards.get(3));
	        		improve.add(cards.get(4));
	        	}
	        	//{x,a,a,y,z}
	        	else if(cards.get(1).getRank() == cards.get(2).getRank()){
	        		improve.add(cards.get(0));
	        		improve.add(cards.get(3));
	        		improve.add(cards.get(4));
	        	}
	        	//{x,y,a,a,z}
	        	else if(cards.get(2).getRank() == cards.get(3).getRank()){
	        		improve.add(cards.get(0));
	        		improve.add(cards.get(1));
	        		improve.add(cards.get(4));
	        	}
	        	//{x,y,z,a,a}
	        	else if(cards.get(3).getRank() == cards.get(4).getRank()){
	        		improve.add(cards.get(0));
	        		improve.add(cards.get(1));
	        		improve.add(cards.get(2));
	        	}
	        	// this means we do not have any pairs of a three of a kin
	        	
	        }else{
        		LOG.info("AI all ");
        		improve.addAll(cards);
        	}
	        	
	        LOG.info("AI wiill stay? {}", improve.isEmpty());
			
	        return improve;
	
	}


	

}
