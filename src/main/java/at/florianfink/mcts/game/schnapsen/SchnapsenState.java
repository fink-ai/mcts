package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.State;
import lombok.*;

import java.util.ArrayList;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@With
public class SchnapsenState implements State {

    private ArrayList<Card> stockCards = new ArrayList<>();
    private Player stockClosedBy = null;

    private Player playerOne = new Player(Player.PlayerIdentifier.ONE);
    private Player playerTwo = new Player(Player.PlayerIdentifier.TWO);

    private ArrayList<Trick> history = new ArrayList<>();

    public Player getActivePlayer() {
        if (history.isEmpty()) {
            return playerOne; // TODO: possible to start game with player 2 active?
        }
        Trick lastTrick = getLastTrick();
        return getPlayerByIdentifier(
                lastTrick.getLeaderCard() == null || lastTrick.getResponderCard() != null ?
                        lastTrick.getLeader()
                        : lastTrick.getResponder()
        );
    }

    public Trick getLastTrick() {
        return history.get(history.size() - 1);
    }

    public int getScore(Player player) {
        return history.stream()
                .filter(trick ->
                        trick.getWinner() == player.getIdentifier()
                                && trick.getLeaderCard() != null
                                && trick.getResponderCard() != null
                )
                .map(trick ->
                        trick.getLeaderCard().getValue()
                                + trick.getResponderCard().getValue()
                )
                .mapToInt(Integer::valueOf).sum()
                + history.stream()
                .filter(trick -> trick.getLeader() == player.getIdentifier())
                .map(trick -> getValueForMeld(trick.getMeld()))
                .mapToInt(Integer::valueOf).sum();
    }

    public Player getWinner() {
        if (playerOne.getCards().isEmpty() && playerTwo.getCards().isEmpty()) {
            if (stockCards.isEmpty())
                return getPlayerByIdentifier(getLastTrick().getWinner());
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

    public Card.Suit getTrumpSuit() {
        return getStockTrumpCard().getSuit();
    }

    public int getValueForMeld(Meld meld) {
        return meld != null ?
                (meld.getSuit() == getTrumpSuit() ? 40 : 20)
                : 0;
    }

    @Override
    public boolean isTerminal() {
        return getWinner() != null;
    }

    public Player getPlayerByIdentifier(Player.PlayerIdentifier playerIdentifier) {
        return playerIdentifier == Player.PlayerIdentifier.ONE ?
                playerOne
                : playerTwo;
    }

    public Player getOpponent(Player player) {
        if (player.getIdentifier() == Player.PlayerIdentifier.ONE) return playerTwo;
        if (player.getIdentifier() == Player.PlayerIdentifier.TWO) return playerOne;
        return null;
    }
}
