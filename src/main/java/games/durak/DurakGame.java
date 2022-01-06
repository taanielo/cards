package games.durak;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import cards.standard.StandardTrumpCard;
import cards.standard.Suit;
import engine.Card;
import engine.CardDeck;
import engine.Game;

@Slf4j
public class DurakGame implements Game {

    private final List<DurakPlayer> players;
    private final DurakExtendedCardDeck deck;
    private DurakPlayer mainAttacker;
    private DurakPlayer defender;
    private int roundNr;

    public DurakGame(List<DurakPlayer> players) {
        deck = new DurakExtendedCardDeck();
        this.players = players;
    }

    @Override
    public void start() {
        deck.createMainDeck();

        // find trump card
        Card<StandardTrumpCard> trumpCard = deck.getCards().get(deck.getCards().size() - 1);
        log.info("Trump card: {}", trumpCard);
        Suit trumpSuit = ((StandardTrumpCard)trumpCard).getSuit();
        // set all same suit cards as trump
        deck.getCards().stream()
                .map(StandardTrumpCard.class::cast)
                .filter(card -> card.getSuit() == trumpSuit)
                .forEach(StandardTrumpCard::setAsTrump);

        for (DurakPlayer player : players) {
            List<Card<StandardTrumpCard>> startingHand = createStartingHandFromDeck();
            player.setStartingHand(startingHand);
        }

        mainAttacker = findFirstPlayer();
        int attackerPosition = players.indexOf(mainAttacker);
        int defenderPosition = attackerPosition + 1 >= players.size()
                ? 0
                : attackerPosition + 1;
        defender = players.get(defenderPosition);
        for (DurakPlayer player : players) {
            log.debug("Player {} cards: {}", player, player.getCardDeck());
        }
        log.debug("First player: {}", mainAttacker.getName());
        roundNr = 1;
    }

    @Override
    public void playRound() {
        log.debug("-- Round {} start --", roundNr);

        Card<StandardTrumpCard> attackCard = mainAttacker.findLowestCardToPlay();
        log.info("Player {} plays {}", mainAttacker, attackCard);
        Optional<Card<StandardTrumpCard>> defendingCard = defender.findDefendingCard(attackCard);
        if (defendingCard.isEmpty()) {
            log.info("Player {} picks up {}", defender, attackCard);
            defender.pickUpCard(attackCard);
        } else {
            log.info("Player {} plays {}", defender, defendingCard.get());
            defender.removeCard(defendingCard.get());
        }
        for (DurakPlayer player : players) {
            log.debug("Player {} cards: {}", player, player.getCardDeck());
        }

        roundNr++;
    }

    @Override
    public boolean isOver() {
        return players.stream()
                .filter(durakPlayer -> durakPlayer.getCardDeck().getCards().size() == 0)
                .toList()
                .size() == 1;
    }

    private List<Card<StandardTrumpCard>> createStartingHandFromDeck() {
        List<Card<StandardTrumpCard>> startingHand = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            startingHand.add(deck.getAndRemoveTopCard());
        }
        return startingHand;
    }

    private DurakPlayer findFirstPlayer() {
        DurakPlayer playerWithWeakestTrumpCard = null;
        StandardTrumpCard weakestTrump = null;
        for (DurakPlayer player : players) {
            CardDeck<StandardTrumpCard> playerCardDeck = player.getCardDeck();
            Optional<StandardTrumpCard> playerWeakestTrumpCard = playerCardDeck.getCards().stream()
                    .map(StandardTrumpCard.class::cast)
                    .filter(StandardTrumpCard::isTrump)
                    .sorted()
                    .findFirst();
            if (playerWeakestTrumpCard.isPresent()) {
                StandardTrumpCard card = playerWeakestTrumpCard.get();
                log.debug("Player {} has lowest trump card {}", player, card);
                if (weakestTrump == null || weakestTrump.compareTo(card) > 0) {
                    weakestTrump = card;
                    playerWithWeakestTrumpCard = player;
                }
            }
        }
        return playerWithWeakestTrumpCard;
    }
}
