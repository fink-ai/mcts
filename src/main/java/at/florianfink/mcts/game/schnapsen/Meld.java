package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

@Data
public class Meld {
    private final SchnapsenState.Player player;
    private final Card.Suit suit;
}