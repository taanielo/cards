package engine;

import java.util.List;

public interface CardDeck<T> {

    List<Card<T>> getCards();

    void remove(Card<T> card);

}
