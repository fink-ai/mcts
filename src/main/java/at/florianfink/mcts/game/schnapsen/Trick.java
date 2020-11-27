package at.florianfink.mcts.game.schnapsen;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Trick {
    private final Player.PlayerIdentifier leader;
    private final Player.PlayerIdentifier responder;
    private final Player.PlayerIdentifier winner;

    private final Card leaderCard;
    private final Card responderCard;

    private final Meld meld;

    private final boolean closeStock;

    public boolean isComplete() {
        return winner != null;
    }
}