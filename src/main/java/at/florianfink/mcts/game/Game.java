package at.florianfink.mcts.game;

import java.util.List;

public interface Game {

    State initializeGame();

    List<Action> getAllowedActions(State currentState);

    State getNextState(State currentState, Action action);

    double getReward(TerminalState state);
}
