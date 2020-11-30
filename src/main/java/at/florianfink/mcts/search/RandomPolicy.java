package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;

import java.util.List;
import java.util.Random;

public class RandomPolicy implements Policy<Game<State, Action>, State, Action> {

    public double getReward(Game<State, Action> game, State state) {
        while (!state.isTerminal()) {
            List<Action> allowedActions = game.getAllowedActions(state);

            Random rand = new Random();
            Action randomAction = allowedActions.get(rand.nextInt(allowedActions.size()));

            state = game.getNextState(state, randomAction);
        }

        return game.getReward(state);
    }
}
