import java.util.List;

import lombok.extern.slf4j.Slf4j;

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
        DurakPlayer player1 = new DurakPlayer("John");
        DurakPlayer player2 = new DurakPlayer("Bob");
        DurakGame game = new DurakGame(List.of(player1, player2));
        game.start();
        do {
            game.playRound();
        } while (!game.isOver());
    }
}
