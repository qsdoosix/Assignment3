package ca.carleton.poker.game.entity.card;

import static org.apache.commons.lang3.StringUtils.capitalize;

/**
 * The rank of a card.
 * <p/>
 * 
 */
public enum Rank {
    ACE_LOW(1, "a"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(10, "j"),
    QUEEN(10, "q"),
    KING(10, "k"),
    ACE_HIGH(11, "a");

    private final int value;

    private final String html;

    Rank(final int value, final String html) {
        this.value = value;
        this.html = html;
    }

    @Override
    public String toString() {
        return capitalize(this.name().toLowerCase());
    }

    public int getValue() {
        return this.value;
    }

    public String getHtml() {
        return this.html;
    }

	public static Rank fromString(String r) {
		switch(r){
			case "1":
				return Rank.ACE_LOW;
			case "2":
				return Rank.TWO;
			case "3":
				return Rank.THREE;
			case "4":
				return Rank.FOUR;
			case "5":
				return Rank.FIVE;
			case "6":
				return Rank.SIX;
			case "7":
				return Rank.SEVEN;
			case "8":
				return Rank.EIGHT;
			case "9":
				return Rank.NINE;
			case "10":
				return Rank.TEN;
			case "j":
				return Rank.JACK;
			case "q":
				return Rank.QUEEN;
			case "k":
				return Rank.KING;
			case "a":
				return Rank.ACE_HIGH;
			default:
				return null;				
		}
	}
}
