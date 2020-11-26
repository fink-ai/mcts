package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.State;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

@Data
public class SchnapsenState implements State {

    public enum Player {
        ONE,
        TWO
    }

    private ArrayList<Card> stockCards = new ArrayList<>();
    private Player stockClosedBy = null;

    private HashSet<Card> playerOneCards = new HashSet<>();
    private HashSet<Card> playerTwoCards = new HashSet<>();

    private ArrayList<Trick> history = new ArrayList<>();

    public Player getActivePlayer() {
        if (history.isEmpty()) {
            return Player.ONE; // TODO: possible to start game with player 2 active?
        }
        Trick lastTrick = getLastTrick();
        return lastTrick.getLeaderCard() == null || lastTrick.getResponderCard() != null ?
                lastTrick.getLeader()
                : lastTrick.getResponder();
    }

    private Trick getLastTrick() {
        return history.get(history.size() - 1);
    }

    public int getScore(Player player) {
        return history.stream()
                .filter(trick ->
                        trick.getWinner() == player
                                && trick.getLeaderCard() != null
                                && trick.getResponderCard() != null
                )
                .map(trick ->
                        trick.getLeaderCard().getValue()
                                + trick.getResponderCard().getValue()
                )
                .mapToInt(Integer::valueOf).sum()
                + history.stream()
                .filter(trick -> trick.getLeader() == player)
                .map(trick -> getValueForMeld(trick.getMeld()))
                .mapToInt(Integer::valueOf).sum();
    }

    public Player getWinner() {
        if (playerOneCards.isEmpty() && playerTwoCards.isEmpty() ) {
            if(stockCards.isEmpty())
                return getLastTrick().getWinner();
            if (stockClosedBy != null && getScore(stockClosedBy) < 66) {
                return getOpponent(stockClosedBy);
            }
        }

        Player activePlayer = getActivePlayer();
        if (getScore(activePlayer) >= 66) {
            return activePlayer;
        }

        return null;
    }

    public Card getStockTrumpCard() {
        return stockCards.get(stockCards.size() - 1);
    }

    public int getValueForMeld(Meld meld) {
        return meld != null ?
                (meld.getSuit() == getStockTrumpCard().getSuit() ? 40 : 20)
                : 0;
    }

    @Override
    public boolean isTerminal() {
        return getWinner() != null;
    }

    public Player getOpponent(Player player) {
        if (player == Player.ONE) return Player.TWO;
        if (player == Player.TWO) return Player.ONE;
        return null;
    }
}
