package cards.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import engine.Card;

@Getter
@AllArgsConstructor
public class StandardCard implements Card<StandardCard> {

    private static final String[] SYMBOLS_SPADES = {"🂢", "🂣", "🂤", "🂥", "🂦", "🂧", "🂨", "🂩", "🂪", "🂫", "🂬", "🂭", "🂮", "🂡"};
    private static final String[] SYMBOLS_HEARTS = {"🂲", "🂳", "🂴", "🂵", "🂶", "🂷", "🂸", "🂹", "🂺", "🂻", "🂼", "🂽", "🂾", "🂱"};
    private static final String[] SYMBOLS_DIAMONDS = {"🃂", "🃃", "🃄", "🃅", "🃆", "🃇", "🃈", "🃉", "🃊", "🃋", "🃌", "🃍", "🃎", "🃁"};
    private static final String[] SYMBOLS_CLUBS = {"🃒", "🃓", "🃔", "🃕", "🃖", "🃗", "🃘", "🃙", "🃚", "🃛", "🃜", "🃝", "🃞", "🃑"};

    private final Integer rank;
    private final Suit suit;

    @Override
    public int compareTo(Card<StandardCard> card) {
        StandardCard comparedCard = (StandardCard)card;
        return rank.compareTo(comparedCard.getRank());
    }

    public static String getName(int rank) {
        if (rank == 12) {
            return "ACE";
        } else if (rank == 11) {
            return "KING";
        } else if (rank == 10) {
            return "QUEEN";
        } else if (rank == 9) {
            return "JACK";
        } else {
            return String.valueOf(rank + 2);
        }
    }

    public static String getSymbol(int rank, Suit suit) {
        String[] symbols;
        String redAnsiColor = "\u001b[30m";
        String resetColor = "\u001b[0m";
        if (suit == Suit.SPADES) {
            symbols = SYMBOLS_SPADES;
        } else if (suit == Suit.HEARTS) {
            symbols = SYMBOLS_HEARTS;
            redAnsiColor = "\u001b[31m";
        } else if (suit == Suit.DIAMONDS) {
            symbols = SYMBOLS_DIAMONDS;
            redAnsiColor = "\u001b[31m";
        } else {
            symbols = SYMBOLS_CLUBS;
        }
        return redAnsiColor + symbols[rank] + resetColor;
    }

}
