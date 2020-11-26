package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

@Data
public class Trick {
    private SchnapsenState.Player leader;
    private SchnapsenState.Player responder;
    private SchnapsenState.Player winner;

    private Card leaderCard;
    private Card responderCard;

    private Meld meld;

    private boolean closeStock;

    public boolean isOpen() {
        return winner == null;
    }
}