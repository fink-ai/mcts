package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.PlayerIdentifier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Trick {
    private final PlayerIdentifier leader;
    private final PlayerIdentifier responder;
    private final PlayerIdentifier winner;

    private final Card leaderCard;
    private final Card responderCard;

    private final Meld meld;

    private final boolean closeStock;
}