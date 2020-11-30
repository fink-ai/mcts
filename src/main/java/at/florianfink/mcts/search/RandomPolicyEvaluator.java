package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;

import java.util.List;
import java.util.Random;

public class RandomPolicyEvaluator<TGame extends Game<TState, TAction>, TState extends State, TAction extends Action>
        implements Evaluator<TGame, TState, TAction> {

    private Random rand = new Random();

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public double getReward(TGame game, TState state) {
        while (!state.isTerminal()) {
            List<TAction> allowedActions = game.getAllowedActions(state);
            TAction randomAction = allowedActions.get(rand.nextInt(allowedActions.size()));

            state = game.getNextState(state, randomAction);
        }

        return game.getReward(state);
    }
}
