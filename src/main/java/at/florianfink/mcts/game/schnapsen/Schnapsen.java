package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;

import java.util.Collections;
import java.util.List;

public class Schnapsen implements Game<SchnapsenState> {
    @Override
    public State initializeGame() {
        SchnapsenState initialState = new SchnapsenState();

        List<Card> deck = Card.generateDeck(2, 3, 4, 10, 11);
        Collections.shuffle(deck);

        for (int i = 0; i < 5; i++) {
            Card top = deck.remove(0);
            initialState.getPlayerOneCards().add(top);
            top = deck.remove(0);
            initialState.getPlayerTwoCards().add(top);
        }

        initialState.getStockCards().addAll(deck);

        return initialState;
    }

    @Override
    public List<Action> getAllowedActions(SchnapsenState currentState) {
        // TODO does action hold active player
        /*
        get active player
        actions comprise:
            if !trick.isOpen
                all cards
                all melds
                close stock
            else
                all legal cards (all cards if stock open)

         */

        return null;
    }

    @Override
    public State getNextState(SchnapsenState currentState, Action action) {
        return null;
    }

    @Override
    public double getReward(SchnapsenState state) {
        assert state.isTerminal();

        int loserScore = state.getScore(state.getOpponent(state.getWinner()));

        if(loserScore == 0) return 3;
        if(loserScore < 33) return 2;
        return 1;

        //TODO: failed closing
    }
}
