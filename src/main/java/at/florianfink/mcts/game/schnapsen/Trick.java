package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

@Data
public class Trick {
    private Player.PlayerIdentifier leader;
    private Player.PlayerIdentifier responder;
    private Player.PlayerIdentifier winner;

    private Card leaderCard;
    private Card responderCard;

    private Meld meld;

    private boolean closeStock;

    public boolean isOpen() {
        return winner == null;
    }
}