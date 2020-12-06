package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.PlayerIdentifier;
import at.florianfink.mcts.game.State;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static at.florianfink.mcts.game.PlayerIdentifier.ONE;
import static at.florianfink.mcts.game.PlayerIdentifier.TWO;

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

    private PlayerIdentifier activePlayer = ONE;
    private Set<Card> playerOneCards = new HashSet<>();
    private Set<Card> playerTwoCards = new HashSet<>();

    private ArrayList<Trick> history = new ArrayList<>();

    public SchnapsenState(SchnapsenState schnapsenState) {
        stockCards = new ArrayList<>(schnapsenState.stockCards);
        stockClosedBy = schnapsenState.stockClosedBy;
        opponentScoreAtStockClosing = schnapsenState.opponentScoreAtStockClosing;
        trumpSuit = schnapsenState.trumpSuit;
        activePlayer = null; // to be set by Schnapsen::getNextState

        playerOneCards.addAll(schnapsenState.getCards(ONE));
        playerTwoCards.addAll(schnapsenState.getCards(TWO));

        history = new ArrayList<>(schnapsenState.history);
    }

    public Trick getLastTrick() {
        return history.get(history.size() - 1);
    }

    public int getScore(PlayerIdentifier playerIdentifier) {
        return history.stream()
                .filter(trick ->
                        trick.getWinner().equals(playerIdentifier)
                                && trick.getLeaderCard() != null
                                && trick.getResponderCard() != null
                )
                .map(trick ->
                        trick.getLeaderCard().getValue()
                                + trick.getResponderCard().getValue()
                )
                .mapToInt(Integer::valueOf).sum()
                + history.stream()
                .filter(trick -> trick.getLeader().equals(playerIdentifier))
                .map(trick -> getValueForMeld(trick.getMeld()))
                .mapToInt(Integer::valueOf).sum();
    }

    public PlayerIdentifier getWinner() {
        if (getCards(ONE).isEmpty() && getCards(TWO).isEmpty()) {
            if (stockCards.isEmpty())
                return getLastTrick().getWinner();
            if (stockClosedBy != null && getScore(stockClosedBy) < 66) {
                return stockClosedBy.getOpponent();
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

    public boolean isStockAvailable() {
        return stockClosedBy == null && !stockCards.isEmpty();
    }

    public boolean isActivePlayerLeading() {
        return history.isEmpty() || getLastTrick().getWinner() != null;
    }

    public Set<Card> getCards(PlayerIdentifier player) {
        if (player.equals(ONE))
            return playerOneCards;
        else
            return playerTwoCards;
    }
}
