package ca.carleton.poker.game.entity.card;

import static org.apache.commons.lang3.StringUtils.capitalize;

/**
 * The suit of a card.
 * <p/>
 * 
 */
public enum Suit {  
    DIAMONDS("diams"),
    CLUBS("clubs"),
    HEARTS("hearts"),
    SPADES("spades");

    private final String html;

    Suit(final String html) {
        this.html = html;
    }

    public String getHtml() {
        return this.html;
    }

    @Override
    public String toString() {
        return capitalize(this.name().toLowerCase());
    }

	public static Suit fromString(String s) {
		switch(s){
		case "hearts": 
			return Suit.HEARTS;
		case "diams":
			return Suit.DIAMONDS;
		case "spades":
			return Suit.SPADES;
		case "clubs":
			return Suit.CLUBS;
		default:
			return null;
			
		}
	}
	
	public int getValue(Suit r){
		switch(r){
			case DIAMONDS:   
				return 0;
			case CLUBS:
				return 13;
			case HEARTS:
				return 26;
			case SPADES:
				return 39;
			default:
				return -1;
		}	
	}
    
}
