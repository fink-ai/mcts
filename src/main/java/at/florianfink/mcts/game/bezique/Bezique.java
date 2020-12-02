package at.florianfink.mcts.game.bezique;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.PlayerIdentifier;
import at.florianfink.mcts.game.State;

import java.util.Set;

public class Bezique implements Game { // TODO parameterize
    @Override
    public State initializeGame() {
        return null;
    }

    @Override
    public Set<Action> getAllowedActions(State currentState) {
        return null;
    }

    @Override
    public State getNextState(State currentState, Action action) {
        return null;
    }

    @Override
    public double getReward(State state) {
        return 0;
    }

    @Override
    public PlayerIdentifier getCurrentPlayer() {
        return null;
    }
}
