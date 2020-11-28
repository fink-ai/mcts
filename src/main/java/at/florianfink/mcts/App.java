package at.florianfink.mcts;

import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.schnapsen.Schnapsen;
import at.florianfink.mcts.game.schnapsen.SchnapsenAction;
import at.florianfink.mcts.game.schnapsen.SchnapsenState;
import at.florianfink.mcts.search.UctSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        UctSearch<Schnapsen, SchnapsenState, SchnapsenAction> uct = new UctSearch<>();
        uct.testMethod(new Schnapsen());

        Game<SchnapsenState, SchnapsenAction> game = new Schnapsen();
        List<SchnapsenState> states = new ArrayList<>();
        states.add(game.initializeGame());

        List<SchnapsenAction> allowedActions;
        SchnapsenAction selectedAction;
        SchnapsenState lastState = last(states);
        while (!lastState.isTerminal() && states.size() < 50) {
            try {
                allowedActions = game.getAllowedActions(lastState).stream()
                        .filter(action -> !(action).isCloseStock())
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

    private static SchnapsenState last(List<SchnapsenState> list) {
        return list.get(list.size() - 1);
    }


}
