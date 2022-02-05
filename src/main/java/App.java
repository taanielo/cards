import lombok.extern.slf4j.Slf4j;

import games.durak.DurakExtendedCardDeck;
import games.durak.DurakGame;
import games.durak.DurakPlayer;

@Slf4j
public class App {

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        log.info("Initializing ..");
        DurakPlayer john = new DurakPlayer("John");
        DurakPlayer bob = new DurakPlayer("Bob");
        DurakExtendedCardDeck cardDeck = new DurakExtendedCardDeck();
        DurakGame game = new DurakGame(cardDeck, john, bob);
        game.start();
        game.play();
    }
}
