package cards.standard;

import java.util.function.Function;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import engine.Card;

@Getter
@EqualsAndHashCode
public class StandardTrumpCard implements Card<StandardTrumpCard> {

    private final Integer rank;
    private final Suit suit;
    private boolean trump;

    public StandardTrumpCard(Integer rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public void setAsTrump() {
        trump = true;
    }

    public int getGameRank() {
        if (trump) {
            return 100 + rank;
        } else {
            return rank;
        }
    }

    public static Function<Card<StandardTrumpCard>,StandardTrumpCard> normalize() {
        return StandardTrumpCard.class::cast;
    }

    @Override
    public int compareTo(Card<StandardTrumpCard> card) {
        StandardTrumpCard comparableCard = (StandardTrumpCard)card;
        if (trump && !comparableCard.isTrump()) {
            return 1;
        }
        return rank.compareTo(comparableCard.getRank());
    }

    @Override
    public String toString() {
        return "[" + StandardCard.getName(rank) + " of " + suit + "," + StandardCard.getSymbol(rank, suit) + "]";
    }

}
