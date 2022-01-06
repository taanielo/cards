package games.durak;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import cards.standard.StandardTrumpCard;
import engine.Card;
import engine.CardDeck;
import engine.Player;

public class DurakPlayer implements Player {

    private final String name;
    private final DurakExtendedCardDeck cardDeck;
    private boolean skipRound = false;

    public DurakPlayer(String name) {
        this.name = name;
        this.cardDeck = new DurakExtendedCardDeck();
    }

    public void setStartingHand(List<Card<StandardTrumpCard>> cards) {
        for (Card<StandardTrumpCard> card : cards) {
            cardDeck.add(card);
        }
    }

    public void pickUpCard(Card<StandardTrumpCard> card) {
        cardDeck.add(card);
    }

    public void removeCard(Card<StandardTrumpCard> card) {
        cardDeck.remove(card);
    }

    public Card<StandardTrumpCard> findLowestCardToPlay() {
        Card<StandardTrumpCard> cardToPlay = cardDeck.getCards().stream()
                .min(findLowestGameCard())
                .orElseThrow(() -> new IllegalStateException("Player has no cards!"));
        cardDeck.remove(cardToPlay);
        return cardToPlay;
    }

    public Optional<Card<StandardTrumpCard>> findDefendingCard(Card<StandardTrumpCard> attackCard) {
        Predicate<Card<StandardTrumpCard>> suitPredicate = ((StandardTrumpCard)attackCard).isTrump()
                ? (card) -> ((StandardTrumpCard)card).isTrump()
                : (card) -> ((StandardTrumpCard)card).isTrump() || ((StandardTrumpCard)card).getSuit().equals(((StandardTrumpCard)attackCard).getSuit());
        return cardDeck.getCards().stream()
                .filter(suitPredicate)
                .filter(card -> ((StandardTrumpCard)card).getGameRank() > ((StandardTrumpCard)attackCard).getGameRank())
                .min(findLowestGameCard());
    }

    public void setSkipRound(boolean skipRound) {
        this.skipRound = skipRound;
    }

    public boolean isSkippingRound() {
        return skipRound;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CardDeck<StandardTrumpCard> getCardDeck() {
        return cardDeck;
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }

    private static Comparator<Card<StandardTrumpCard>> findLowestGameCard() {
        return Comparator.comparingInt(value -> ((StandardTrumpCard)value).getGameRank());
    }

}
