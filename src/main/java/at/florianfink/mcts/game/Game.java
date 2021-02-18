package at.florianfink.mcts.game;

import java.util.Random;
import java.util.Set;

public interface Game<TState extends State, TAction extends Action> {

    TState initializeGame(Random rand);

    Set<TAction> getAllowedActions(TState currentState);

    TState getNextState(TState currentState, TAction action);

    double getReward(TState state);

    void determinizeHiddenInformation(TState state, Random random);

    TState cloneState(TState state);
}
