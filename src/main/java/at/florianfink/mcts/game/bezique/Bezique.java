package at.florianfink.mcts.game.bezique;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;
import at.florianfink.mcts.game.TerminalState;

import java.util.List;

public class Bezique implements Game {
    @Override
    public State initializeGame() {
        return null;
    }

    @Override
    public List<Action> getAllowedActions(State currentState) {
        return null;
    }

    @Override
    public State getNextState(State currentState, Action action) {
        return null;
    }

    @Override
    public double getReward(TerminalState state) {
        return 0;
    }
}
