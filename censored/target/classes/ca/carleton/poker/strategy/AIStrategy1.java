package ca.carleton.poker.strategy;

import ca.carleton.poker.game.Player.*;
import ca.carleton.poker.game.entity.card.Card;
import ca.carleton.poker.game.entity.card.PokerHand;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * - strategy 1
 */

public class AIStrategy1 implements AIStrategy {
	
    static final Logger LOG = LoggerFactory.getLogger(AIStrategy1.class);
    private AIPlayer player; 
    // We want to try to get a straight.
    // 
   public AIStrategy1(AIPlayer p){
	   this.player = p;
   }
    
    /* 
     * attempt to get a full house by exchanging everything not a pair or 3 of a kind
     * 
    */ 
    public List<Card> chooseCard(){
    	List<Card> improve = new ArrayList<>();
        List<Card> cards = this.player.getHand().sortRank();
        PokerHand handvalue = this.player.getHand().getPokerValue();
        improve.clear();
        
        
        
        // check if use has greater than Straight and stay. 
		  if(handvalue.compareTo(PokerHand.STRAIGHT) >= 0 ){
			  return improve;
	
		  }	  
        /* if its a three of a kind, 
         * keep three of a kind 
        */
        else if (handvalue.equals(PokerHand.THREE_OF_A_KIND)){
        	//if {a,a,a,b,x}
        	if(cards.get(0).getRank() == cards.get(1).getRank() &&
        		cards.get(1).getRank()== cards.get(2).getRank()){
        		improve.add(cards.get(3));
        		improve.add(cards.get(4));
        	}
        	// {b,x,a,a,a}
        	else{
        		improve.add(cards.get(0));
        		improve.add(cards.get(1));
        	}
        }else if(handvalue.equals(PokerHand.TWO_PAIR)){
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
    		improve.addAll(cards);
    	}
        return improve;
   }

}
