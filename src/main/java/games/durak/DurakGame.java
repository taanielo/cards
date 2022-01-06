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

/**
 * Durak card game - https://en.wikipedia.org/wiki/Durak
 */
@Slf4j
public class DurakGame implements Game {

    private final List<DurakPlayer> players;
    private final DurakExtendedCardDeck deck;
    private DurakExtendedCardDeck tableCards;
    private int cardsKilledInRound;
    private DurakPlayer mainAttacker;
    private DurakPlayer defender;
    private int roundNr;

    public DurakGame(List<DurakPlayer> players) {
        deck = new DurakExtendedCardDeck();
        tableCards = new DurakExtendedCardDeck();
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
                .map(StandardTrumpCard.normalize())
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
        cardsKilledInRound = 0;

        Card<StandardTrumpCard> attackCard = mainAttacker.findLowestCardToPlay()
                .orElseThrow(() -> new IllegalStateException("No cards to play! Game over!"));
        tableCards.add(attackCard);
        log.info("Player {} attacks {}", mainAttacker, attackCard);
        checkWinner();
        Optional<StandardTrumpCard> defendingCardOpt = defender.findDefendingCard(attackCard);
        if (defendingCardOpt.isEmpty()) {
            log.info("Player {} picks up {}", defender, attackCard);
            defender.pickCard(attackCard);
            // TODO other players can add more cards to pick up
        } else {
            StandardTrumpCard defendingCard = defendingCardOpt.get();
            log.info("Player {} defends {}", defender, defendingCard);
            defender.removeCard(defendingCard);
            tableCards.add(defendingCard);
            cardsKilledInRound++;

            // switch attacker and defender
            DurakPlayer currentDefender = defender;
            defender = mainAttacker;
            mainAttacker = currentDefender;

            // TODO attackers can add more cards to play until cardsKilledInRound <= 6
        }
        for (DurakPlayer player : players) {
            log.debug("Player {} cards: {}", player, player.getCardDeck());
        }

        players.forEach(player -> DurakAction.takeNewCards(player, deck));
        checkWinner();

        roundNr++;
    }

    @Override
    public boolean isOver() {
        return players.stream()
                .anyMatch(durakPlayer -> durakPlayer.getCardDeck().getCards().isEmpty());
    }

    private List<Card<StandardTrumpCard>> createStartingHandFromDeck() {
        List<Card<StandardTrumpCard>> startingHand = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Optional<Card<StandardTrumpCard>> topCard = deck.getAndRemoveTopCard();
            topCard.ifPresent(startingHand::add);
        }
        return startingHand;
    }

    private DurakPlayer findFirstPlayer() {
        DurakPlayer playerWithWeakestTrumpCard = null;
        StandardTrumpCard weakestTrump = null;
        for (DurakPlayer player : players) {
            CardDeck<StandardTrumpCard> playerCardDeck = player.getCardDeck();
            Optional<StandardTrumpCard> playerWeakestTrumpCard = playerCardDeck.getCards().stream()
                    .map(StandardTrumpCard.normalize())
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

    /**
     * TODO actually game has loser - Durak, and all others as winners
     */
    private void checkWinner() {
        // check winner
        Optional<DurakPlayer> winner = players.stream()
                .filter(player -> player.getCardDeck().getCards().isEmpty())
                .findFirst();
        if (winner.isPresent()) {
            log.info("Player {} wins the game!", winner.get());
        }
    }

}
