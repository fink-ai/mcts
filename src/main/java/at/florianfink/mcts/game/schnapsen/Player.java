package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.PlayerIdentifier;
import lombok.Data;

import java.util.HashSet;

@Data
public class Player {


    private HashSet<Card> cards = new HashSet<>();

    private final PlayerIdentifier identifier;
}
