package games.durak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import cards.standard.StandardCard;
import cards.standard.StandardTrumpCard;
import cards.standard.Suit;
import engine.Card;
import engine.CardDeck;

@Slf4j
public class DurakExtendedCardDeck implements CardDeck<StandardTrumpCard> {

    private List<Card<StandardTrumpCard>> cards = new ArrayList<>();

    public void createMainDeck() {
        cards = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (int rank = 0; rank < 13; rank++) {
                cards.add(new StandardTrumpCard(rank, suit));
            }
        }
        cards = new ArrayList<>(cards);
        Collections.shuffle(cards);
    }

    public Optional<Card<StandardTrumpCard>> getAndRemoveTopCard() {
        if (cards.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cards.remove(0));
    }

    public void add(Card<StandardTrumpCard> card) {
        cards.add(card);
    }

    @Override
    public List<Card<StandardTrumpCard>> getCards() {
        return cards;
    }


    @Override
    public void remove(Card<StandardTrumpCard> card) {
        cards.remove(card);
    }

    @Override
    public String toString() {
        return cards.stream()
                .map(StandardTrumpCard.normalize())
                .sorted(Comparator.comparingInt(StandardTrumpCard::getGameRank))
                .map(card -> StandardCard.getSymbol(card.getRank(), card.getSuit()))
                .collect(Collectors.joining(""));
    }

}
