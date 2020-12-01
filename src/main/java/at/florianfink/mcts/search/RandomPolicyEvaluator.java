package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;
import lombok.Setter;

import java.util.Random;
import java.util.Set;

public class RandomPolicyEvaluator<TGame extends Game<TState, TAction>, TState extends State, TAction extends Action>
        implements Evaluator<TGame, TState, TAction> {

    @Setter
    private Random rand = new Random();

    public double getReward(TGame game, TState state) {
        while (!state.isTerminal()) {
            Set<TAction> allowedActions = game.getAllowedActions(state);
            TAction randomAction = allowedActions.stream()
                    .skip(rand.nextInt(allowedActions.size())).findFirst().orElse(null);

            state = game.getNextState(state, randomAction);
        }

        return game.getReward(state);
    }
}
