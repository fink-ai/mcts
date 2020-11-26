package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Action;
import lombok.Data;

@Data
public class SchnapsenAction implements Action {
    private Card playCard;
    private Meld meld;
    private boolean closeStock;
}
