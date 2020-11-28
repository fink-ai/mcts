package at.florianfink.mcts;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;
import at.florianfink.mcts.game.schnapsen.Schnapsen;
import at.florianfink.mcts.game.schnapsen.SchnapsenAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Game game = new Schnapsen();
        List<State> states = new ArrayList<>();
        states.add(game.initializeGame());

        List<Action> allowedActions;
        Action selectedAction;
        State lastState = last(states);
        while (!lastState.isTerminal() && states.size() < 50) {
            try {
                allowedActions = (List<Action>) game.getAllowedActions(lastState).stream()
                        .filter(action -> !((SchnapsenAction) action).isCloseStock())
                        .collect(Collectors.toList());
                Collections.shuffle(allowedActions);
                selectedAction = allowedActions.get(0);

                states.add(game.getNextState(lastState, selectedAction));
                lastState = last(states);
            } catch (Exception e) {
                System.out.println("well ...");
            }
        }

        System.out.println("hooray it didn't crash!");
    }

    private static State last(List<State> list) {
        return list.get(list.size() - 1);
    }


}
