package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.PlayerIdentifier;
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
    private PlayerIdentifier stockClosedBy = null;
    private int opponentScoreAtStockClosing = 0;
    private Card.Suit trumpSuit = null;

    private PlayerIdentifier activePlayer = PlayerIdentifier.ONE;
    private Player playerOne = new Player(PlayerIdentifier.ONE);
    private Player playerTwo = new Player(PlayerIdentifier.TWO);

    private ArrayList<Trick> history = new ArrayList<>();

    public SchnapsenState(SchnapsenState schnapsenState) {
        stockCards = new ArrayList<>(schnapsenState.stockCards);
        stockClosedBy = schnapsenState.stockClosedBy;
        opponentScoreAtStockClosing = schnapsenState.opponentScoreAtStockClosing;
        trumpSuit = schnapsenState.trumpSuit;
        activePlayer = null; // to be set by Schnapsen::getNextState

        playerOne = new Player(PlayerIdentifier.ONE);
        playerOne.getCards().addAll(schnapsenState.playerOne.getCards());
        playerTwo = new Player(PlayerIdentifier.TWO);
        playerTwo.getCards().addAll(schnapsenState.playerTwo.getCards());

        history = new ArrayList<>(schnapsenState.history);
    }

    public Trick getLastTrick() {
        return history.get(history.size() - 1);
    }

    public int getScore(PlayerIdentifier playerIdentifier) {
        return history.stream()
                .filter(trick ->
                        trick.getWinner() == playerIdentifier
                                && trick.getLeaderCard() != null
                                && trick.getResponderCard() != null
                )
                .map(trick ->
                        trick.getLeaderCard().getValue()
                                + trick.getResponderCard().getValue()
                )
                .mapToInt(Integer::valueOf).sum()
                + history.stream()
                .filter(trick -> trick.getLeader() == playerIdentifier)
                .map(trick -> getValueForMeld(trick.getMeld()))
                .mapToInt(Integer::valueOf).sum();
    }

    public PlayerIdentifier getWinner() {
        if (playerOne.getCards().isEmpty() && playerTwo.getCards().isEmpty()) {
            if (stockCards.isEmpty())
                return getLastTrick().getWinner();
            if (stockClosedBy != null && getScore(stockClosedBy) < 66) {
                return getOpponent(stockClosedBy);
            }
        }

        PlayerIdentifier activePlayer = getActivePlayer();
        if (getScore(activePlayer) >= 66) {
            return activePlayer;
        }

        return null;
    }

    public int getValueForMeld(Meld meld) {
        return meld != null
                ? (meld.getSuit() == getTrumpSuit() ? 40 : 20)
                : 0;
    }

    @Override
    public boolean isTerminal() {
        return getWinner() != null;
    }

    public Player getPlayerByIdentifier(PlayerIdentifier playerIdentifier) {
        return playerIdentifier == PlayerIdentifier.ONE
                ? playerOne
                : playerTwo;
    }

    public PlayerIdentifier getOpponent(PlayerIdentifier playerIdentifier) {
        if (playerIdentifier == PlayerIdentifier.ONE) return PlayerIdentifier.TWO;
        if (playerIdentifier == PlayerIdentifier.TWO) return PlayerIdentifier.ONE;
        return null;
    }

    public boolean isStockAvailable() {
        return stockClosedBy == null && !stockCards.isEmpty();
    }

    public boolean isActivePlayerLeading() {
        return history.isEmpty() || getLastTrick().getWinner() != null;
    }
}
