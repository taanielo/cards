package cards.standard;

public enum Suit {

    CLUBS("♣"),
    DIAMONDS("♦"),
    HEARTS("♥"),
    SPADES("♠");

    private final String shortSymbol;

    Suit(String shortSymbol) {
        this.shortSymbol = shortSymbol;
    }

    public String getShortSymbol() {
        return shortSymbol;
    }
}
