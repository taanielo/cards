package games.durak;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;

import cards.standard.StandardTrumpCard;
import engine.Game;

/**
 * Durak card game - https://en.wikipedia.org/wiki/Durak
 */
@Slf4j
public class DurakGame implements Game {

    private final List<DurakPlayer> players;
    private final DurakExtendedCardDeck deck;
    private final DurakGameTable gameTable;
    private DurakPlayer mainAttacker;
    private DurakPlayer defender;

    public DurakGame(DurakExtendedCardDeck cardDeck, @Nonnull DurakPlayer player1, @Nonnull DurakPlayer player2) {
        this(cardDeck, List.of(player1, player2));
    }

    public DurakGame(DurakExtendedCardDeck cardDeck, @Nonnull DurakPlayer player1, @Nonnull DurakPlayer player2, @Nonnull DurakPlayer player3) {
        this(cardDeck, List.of(player1, player2, player3));
    }

    public DurakGame(DurakExtendedCardDeck cardDeck, @Nonnull DurakPlayer player1, @Nonnull DurakPlayer player2, @Nonnull DurakPlayer player3,
            @Nonnull DurakPlayer player4) {
        this(cardDeck, List.of(player1, player2, player3, player4));
    }

    private DurakGame(DurakExtendedCardDeck cardDeck, List<DurakPlayer> players) {
        deck = cardDeck;
        gameTable = new DurakGameTable();
        this.players = new ArrayList<>(players);
    }

    @Override
    public void start() {
        deck.createMainDeck();
        players.forEach(player -> player.dealStartingCards(deck));
    }

    @Override
    public void play() {
        log.debug("-- Round {} start --", roundNr);

        StandardTrumpCard attackCard = mainAttacker.findLowestCardToPlay()
                .orElseThrow(() -> new IllegalStateException("No cards to play! Game over!"));
        gameTable.add(attackCard);
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
            gameTable.add(defendingCard);
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

    private boolean isOnlyOnePlayerRemainingWithCards() {
        return players.stream()
                .filter(durakPlayer -> durakPlayer.getCardDeck().getCards().isEmpty())
                .count() == 1;
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
