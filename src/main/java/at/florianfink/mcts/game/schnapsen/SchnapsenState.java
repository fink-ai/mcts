package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.State;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;

@Data
public class SchnapsenState implements State {

    public enum Player {
        ONE,
        TWO
    }

    private ArrayList<Card> stockCards;
    private boolean stockClosed = false;

    private HashSet<Card> playerOneCards;
    private HashSet<Card> playerTwoCards;

    private ArrayList<Trick> history;

    @Data
    public class Trick {
        private final Player leader;
        private final Player winner;

        private final Card leaderCard;
        private final Card responderCard;

        private final Meld meld;
    }

    @Data
    public class Meld {
        private final Player player;
        private final Card.Suit suit;
    }
}
