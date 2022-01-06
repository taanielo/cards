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

    public void pickCard(Card<StandardTrumpCard> card) {
        cardDeck.add(card);
    }

    public void removeCard(Card<StandardTrumpCard> card) {
        cardDeck.remove(card);
    }

    public Optional<Card<StandardTrumpCard>> findLowestCardToPlay() {
        Optional<Card<StandardTrumpCard>> cardToPlay = cardDeck.getCards().stream()
                .min(findLowestGameCard());
        cardToPlay.ifPresent(cardDeck::remove);
        return cardToPlay;
    }

    public Optional<StandardTrumpCard> findDefendingCard(Card<StandardTrumpCard> attackCard) {
        StandardTrumpCard attackCardStd = ((StandardTrumpCard) attackCard);
        Predicate<StandardTrumpCard> suitPredicate = attackCardStd.isTrump()
                ? StandardTrumpCard::isTrump
                : (card) -> card.isTrump() || card.getSuit().equals(attackCardStd.getSuit());
        return cardDeck.getCards().stream()
                .map(StandardTrumpCard.normalize())
                .filter(suitPredicate)
                .filter(card -> card.getGameRank() > attackCardStd.getGameRank())
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
