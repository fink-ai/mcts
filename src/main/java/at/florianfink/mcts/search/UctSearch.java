package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;

import java.util.ArrayList;
import java.util.List;

public class UctSearch<TGame extends Game<TState, TAction>, TState extends State, TAction extends Action> {

    public void testMethod(TGame game) {
        List<TState> states = new ArrayList<>();
        states.add(game.initializeGame());
        // ...
    }

}
