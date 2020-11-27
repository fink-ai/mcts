package at.florianfink.mcts.game;

import java.util.List;

public interface Game<T1 extends State, T2 extends Action> {

    State initializeGame();

    List<Action> getAllowedActions(T1 currentState);

    State getNextState(T1 currentState, T2 action);

    double getReward(T1 state);
}
