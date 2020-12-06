package at.florianfink.mcts.game;

import java.util.Objects;

public class PlayerIdentifier {
    public static final PlayerIdentifier ONE = new PlayerIdentifier(1, "Player One");
    public static final PlayerIdentifier TWO = new PlayerIdentifier(2, "Player Two");

    private final int identifier;
    private final String name;

    private PlayerIdentifier(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public PlayerIdentifier getOpponent() {
        if (identifier == 1)
            return TWO;
        else
            return ONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerIdentifier that = (PlayerIdentifier) o;
        return identifier == that.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return name;
    }
}
