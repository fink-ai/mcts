package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Action;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@With
public class SchnapsenAction implements Action {
    private Card playCard;
    private Meld meld;
    private boolean closeStock;

    public SchnapsenAction(Card playCard) {
        this.playCard = playCard;
    }
}
