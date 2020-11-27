package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

import java.util.HashSet;

@Data
public class Player {
    public enum PlayerIdentifier {
        ONE,
        TWO
    }

    private HashSet<Card> cards = new HashSet<>();

    private final PlayerIdentifier identifier;
}
