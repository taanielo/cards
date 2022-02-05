package games.durak;

import cards.standard.StandardTrumpCard;

public class DurakGameTable {
    private DurakExtendedCardDeck cardsOnTable;

    public DurakGameTable() {
        cardsOnTable = new DurakExtendedCardDeck();
    }

    public void addCard(StandardTrumpCard card) {
        cardsOnTable.add(card);
    }

    public void removeCards() {
        cardsOnTable = new DurakExtendedCardDeck();
    }

}
