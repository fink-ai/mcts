package at.florianfink.mcts.game;

import java.util.List;

public interface Game<TState extends State, TAction extends Action> {

    TState initializeGame();

    List<TAction> getAllowedActions(TState currentState);

    TState getNextState(TState currentState, TAction action);

    double getReward(TState state);
}
