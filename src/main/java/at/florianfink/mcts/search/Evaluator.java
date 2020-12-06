package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.PlayerIdentifier;
import at.florianfink.mcts.game.State;

public interface Evaluator<TGame extends Game<TState, TAction>, TState extends State, TAction extends Action> {
    double getReward(TGame game, TState state, PlayerIdentifier player);
}
