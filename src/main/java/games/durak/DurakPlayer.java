package games.durak;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

import cards.standard.StandardTrumpCard;
import engine.Card;
import engine.Player;

public class DurakPlayer implements Player {

    private final String name;
    private final DurakExtendedCardDeck cardDeck;
    private boolean skipRound = false;

    public DurakPlayer(String name) {
        this.name = name;
        this.cardDeck = new DurakExtendedCardDeck();
    }

    public void playCard(DurakGameTable gameTable) {

    }

    @Override
    public String name() {
        return name;
    }

    public Optional<StandardTrumpCard> findDefendingCard(StandardTrumpCard attackCard) {
        Predicate<StandardTrumpCard> suitPredicate = attackCard.isTrump()
                ? StandardTrumpCard::isTrump
                : (card) -> card.isTrump() || card.getSuit().equals(attackCard.getSuit());
        return cardDeck.getCards().stream()
                .map(StandardTrumpCard.normalize())
                .filter(suitPredicate)
                .filter(card -> card.getGameRank() > attackCard.getGameRank())
                .min(findLowestGameCard());
    }

    public void setSkipRound(boolean skipRound) {
        this.skipRound = skipRound;
    }

    public boolean isSkippingRound() {
        return skipRound;
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }

    public void dealStartingCards(DurakExtendedCardDeck deck) {
        for (int i = 0; i < 6; i++) {
            cardDeck.add(deck.getAndRemoveTopCard().get());
        }
    }

    private static Comparator<Card<StandardTrumpCard>> findLowestGameCard() {
        return Comparator.comparingInt(value -> ((StandardTrumpCard)value).getGameRank());
    }

    private static class CardHand {
        private final Collection<StandardTrumpCard> cards = new HashSet<>();

        private void addCard(StandardTrumpCard card) {
            cards.add(card);
        }

        private Optional<StandardTrumpCard> findLowestCard() {
            return cards.stream()
                    .min(StandardTrumpCard::compareTo);
        }
    }

}
