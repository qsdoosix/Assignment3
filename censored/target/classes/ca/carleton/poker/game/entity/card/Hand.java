package ca.carleton.poker.game.entity.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.seleniumhq.jetty7.util.log.Log;

/**
 * A Hand that a player has.
 * <p/>
 * 
 */
public class Hand {

    private List<Card> cards = new ArrayList<>();

    private HandStatus handStatus;
    private PokerHand value;

    public void addCard(final Card card) {
        this.cards.add(card);
    }
    public void removecard(final Card card) {
        this.cards.remove(card);
    }

    public List<Card> getCards() {
   
        return this.cards;
    }

    public void clearHand() {
        this.cards.clear();
    }


    public long getVisibleHandValue() {
        return this.cards.stream()
                .filter(card -> !card.isHidden())
                .mapToInt(card -> card.getRank().getValue())
                .sum();
    }
  
    public HandStatus getHandStatus() {
        return this.handStatus;
    }

    public void setHandStatus(final HandStatus handStatus) {
        this.handStatus = handStatus;
    }

	public void setPokerValue() {
		if( this.isRFlush()) { this.value = PokerHand.ROYAL_FLUSH; }
		else if (this.isStraightFlush()) {this.value =  PokerHand.STRAIGHT_FLUSH; }
    	else if (this.isFourOfAKind()){this.value = PokerHand.FOUR_OF_A_KIND; }
    	else if (this.isFullHouse()) {this.value =  PokerHand.FULL_HOUSE; }
    	else if (this.isFlush()){this.value = PokerHand.FLUSH; }
    	else if (this.isStraight()){this.value = PokerHand.STRAIGHT; }
    	else if (this.isThreeOfAKind()) {this.value = PokerHand.THREE_OF_A_KIND; }
    	else if (this.isTwoPair()){this.value = PokerHand.TWO_PAIR; }
    	else if (this.isOnePair()) {this.value =  PokerHand.ONE_PAIR; }
    	else{this.value =  PokerHand.HIGH_CARD;}
	}
	public PokerHand getPokerValue(){
		return this.value;
	}

    @Override
    public boolean equals(final Object rhs) {
        if (rhs instanceof Hand) {
            for (final Card otherCard : ((Hand) rhs).getCards()) {
                if (!this.cards.contains(otherCard)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.cards.toString();
    }

	public List<Card> sortSuit() {
		List<Card> sortedCards = new ArrayList<>();
		// clubs 
		for(int i = 0; i < this.cards.size(); i++){
			if(this.cards.get(i).getSuit()== Suit.DIAMONDS){
				sortedCards.add(this.cards.get(i));
			}
		}
		// spades 
		for(int i = 0; i < this.cards.size(); i++){
			if(this.cards.get(i).getSuit()== Suit.CLUBS){
				sortedCards.add(this.cards.get(i));
			}
		}
		
		// diamonds
		for(int i = 0; i < this.cards.size(); i++){
			if(this.cards.get(i).getSuit()== Suit.HEARTS){
				sortedCards.add(this.cards.get(i));
			}
		}
		
		//hearts		
		for(int i = 0; i < this.cards.size(); i++){
			if(this.cards.get(i).getSuit()== Suit.SPADES){
				sortedCards.add(this.cards.get(i));
			}
		}
		return sortedCards;
	}
	
	public List<Card> sortRank(){
		List<Card> sortedCards = new ArrayList<>();
		
		sortedCards = this.cards;
		int i, j, min_j;
		for ( i = 0 ; i < sortedCards.size(); i ++ ){
	         min_j = i;
	         for ( j = i+1 ; j < sortedCards.size() ; j++ ){
	            if (sortedCards.get(j).getRank().compareTo(sortedCards.get(min_j).getRank()) < 0 ){
	               min_j = j;   
	            }
	         }
	         //swap
	         Card help = sortedCards.get(i);
	         sortedCards.set(i, sortedCards.get(min_j));
	         sortedCards.set(min_j, help);

		}
		return sortedCards;
	}

	
	/* Checks for a Royal flush
	 *  - a poker hand that is ranked highest, A, K, Q, J, 10 of hearts
	 */
	public boolean isRFlush(){
		boolean result = false;
		List<Card> cards = sortSuit();// sorts all cards 
		boolean flush = this.isFlush() && cards.get(0).getSuit() == Suit.HEARTS; 
		
		cards = sortRank();// sorts all cards  lowest to highest
		if (cards.get(0).getRank()== Rank.TEN 
				&& cards.get(1).getRank() == Rank.JACK 
				&& cards.get(2).getRank() == Rank.QUEEN 
				&& cards.get(3).getRank()== Rank.KING  
				&& cards.get(4).getRank()== Rank.ACE_HIGH)  
			return true && flush;
	
		return false;
	}
	

	
	/* Checks for a Striaght flush
	 *  - a poker hand that has five cards in a sequence all in the smae suit
	 */
	public boolean isStraightFlush(){
		return this.isFlush() && this.isStraight();
	}
	
	
	/* Checks for a four of a kind
	 *  - checks that the rank of 4 cards is the same
	 */
	public boolean isFourOfAKind(){
		boolean result = false;
		List<Card> cards = sortRank();// sorts all cards
		// because they are sorted by rank the missing card has to be the first or the last
		for(int i = 0; i < 2; i++){
			if(cards.get(i).getRank() == cards.get(i+1).getRank() 
					&& cards.get(i+1).getRank()== cards.get(i+2).getRank()
					&& cards.get(i+2).getRank() == cards.get(i+3).getRank()){
				result = true;	
				break;
			}
		}
		
		return result;
	}
	
	/* Checks for a full house
	 *  - a poker hand that has 3 of same rank and 1 pair
	 *  - suit doesn't matter
	 */
	
	public boolean isFullHouse(){
		List<Card> cards = sortRank();// sorts all cards 
		// because they are sorted by rank the missing card has to be the first or the last
		// if  {a,a,a,b,b)
		boolean a1 = cards.get(0).getRank() == cards.get(1).getRank() &&
					cards.get(1).getRank() == cards.get(2).getRank() &&
					cards.get(3).getRank() == cards.get(4).getRank();
		
		// if {a,a,b,b,b}
		
		boolean a2 = cards.get(0).getRank() == cards.get(1).getRank() &&
				cards.get(2).getRank() == cards.get(3).getRank() &&
				cards.get(3).getRank() == cards.get(4).getRank();

		return a1||a2;
	}
	
	
	/* Checks for a flush
	 *  - a poker hand that  has 5 cards of the same suit, sequence doesn't matter
	 */
	
	public boolean isFlush(){
		List<Card> cards = sortSuit();// sorts all cards 
		// because they are sorted by rank the missing card has to be the first or the last
				
		// check suits are all the same
		return (cards.get(0).getSuit() ==  cards.get(1).getSuit() && 
				cards.get(1).getSuit() == cards.get(2).getSuit()&& 
				cards.get(2).getSuit() == cards.get(3).getSuit() &&  
				cards.get(3).getSuit() == cards.get(4).getSuit());  
	
	}
	
	
	/* Checks for a straight
	 *  - a poker hand that  has 5 cards ranked in a row i.e. 3,4,5,6,7
	 *  - suit doesn't matter
	 */
	public boolean isStraight(){
		boolean result = false;
		List<Card> cards = sortRank();// sorts all cards 
		// because they are sorted by rank the missing card has to be the first or the last
				
		if(cards.get(0).getRank().getValue()+1 == cards.get(1).getRank().getValue()
				&& cards.get(1).getRank().getValue()+1 == cards.get(2).getRank().getValue()
				&& cards.get(2).getRank().getValue()+1 == cards.get(3).getRank().getValue()
				&& cards.get(3).getRank().getValue()+1 == cards.get(4).getRank().getValue())
			result = true;

		return result;
	}
	
	
	/* Checks for a three of a kind
	 *  - checks that the rank of 3 cards is the same
	 */
	public boolean isThreeOfAKind(){
		List<Card> cards = sortRank();// sorts all cards
		if(cards.size() == 3){
			return (cards.get(0).getRank()  == cards.get(1).getRank() 
					&& cards.get(1).getRank() == cards.get(2).getRank());
		}
		if(cards.size() <3)return false;
		
		// because they are sorted by rank the missing card has to be the first or the last
		for(int i = 0; i < 3; i++){
			if(cards.get(i).getRank() == cards.get(i+1).getRank() 
					&& cards.get(i+1).getRank()== cards.get(i+2).getRank()){
					return true;
				}
			}
			return false;
	}
	
	
	/* 
	 * Checks for a 2 different pairs 
	 */
	public boolean isTwoPair(){
	
		List<Card> cards = sortRank();// sorts all cards 

	      /* --------------------------------
	         Checking: a a  b b x
		 -------------------------------- */                       
	      boolean a1 = cards.get(0).getRank() == cards.get(1).getRank() &&
	    		  cards.get(2).getRank() ==  cards.get(3).getRank() ;

	      /* ------------------------------
	         Checking: a a x  b b
		 ------------------------------ */
	     boolean  a2 = cards.get(0).getRank() == cards.get(1).getRank() &&
	    		 cards.get(3).getRank() == cards.get(4).getRank() ;

	      /* ------------------------------
	         Checking: x a a  b b
		 ------------------------------ */
	      boolean a3 =cards.get(1).getRank() == cards.get(2).getRank()&&
	    		  cards.get(3).getRank() == cards.get(4).getRank() ;

	 
	      return( a1 || a2 || a3 );
	}
	/* 
	 * Checks for a 1 different pairs 
	 */
	public boolean isOnePair(){

		List<Card> cards = sortRank();// sorts all cards 

		 /* --------------------------------
        Checking: a a  x x x
	 -------------------------------- */                       
     boolean a1 = cards.get(0).getRank() == cards.get(1).getRank();

     /* ------------------------------
        Checking: x a a  x x
	 ------------------------------ */
    boolean  a2 = cards.get(1).getRank() == cards.get(2).getRank();

     /* ------------------------------
        Checking: x x a a x
	 ------------------------------ */
     boolean a3 =cards.get(2).getRank() == cards.get(3).getRank();

     /* ------------------------------
     Checking: x x x a a
	 ------------------------------ */
     boolean a4 = cards.get(3).getRank() == cards.get(4).getRank();

     return( a1 || a2 || a3 || a4 );
	
	}
	public Card getHighCard() {
		
		Card highest = this.cards.get(0);
		for (int counter = 0; counter < 5; counter++)
		{
			if (this.cards.get(counter).getRank().getValue() > highest.getRank().getValue())
			{
				highest = this.cards.get(counter);
			}
		}
		return highest;
		
	}
	public Hand visibleHand() {
		Hand temp = new Hand();
		for(Card c: this.getCards()){
			if(!c.isHidden()){
				temp.addCard(c);
			}
		}
		return temp;
	}
	
	
	



}
