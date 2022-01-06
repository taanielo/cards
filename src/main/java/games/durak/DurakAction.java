package games.durak;

import java.util.Optional;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import cards.standard.StandardTrumpCard;
import engine.Card;

/**
 * Utility class for different game and round actions
 *
 * @author Taaniel Ots
 */
@Slf4j
@UtilityClass
public class DurakAction {

    int MIN_CARDS_IN_HAND = 6;

    public static void takeNewCards(DurakPlayer player, DurakExtendedCardDeck cardDeck) {
        int cardsToTake = MIN_CARDS_IN_HAND - player.getCardDeck().getCards().size();
        if (cardsToTake > 0 && !cardDeck.getCards().isEmpty()) {
            for (int i = 0; i < cardsToTake; i++) {
                Optional<Card<StandardTrumpCard>> topCard = cardDeck.getAndRemoveTopCard();
                if (topCard.isEmpty()) {
                    break;
                }
                Card<StandardTrumpCard> card = topCard.get();
                log.debug("Player {} takes a new card {}", player, card);
                player.pickCard(card);
            }
        }
    }

}
