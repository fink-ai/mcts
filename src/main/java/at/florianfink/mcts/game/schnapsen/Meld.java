package at.florianfink.mcts.game.schnapsen;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Meld {
    private final Card.Suit suit;
}