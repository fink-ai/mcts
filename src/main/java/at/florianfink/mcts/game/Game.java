package at.florianfink.mcts.game;

import java.util.List;

public interface Game<T extends State> {

    State initializeGame();

    List<Action> getAllowedActions(T currentState);

    State getNextState(T currentState, Action action);

    double getReward(T state);
}
