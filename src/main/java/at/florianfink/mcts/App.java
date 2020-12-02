package at.florianfink.mcts;

import at.florianfink.mcts.game.PlayerIdentifier;
import at.florianfink.mcts.game.schnapsen.Schnapsen;
import at.florianfink.mcts.game.schnapsen.SchnapsenAction;
import at.florianfink.mcts.game.schnapsen.SchnapsenState;
import at.florianfink.mcts.search.UctSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 * if only one action, immediately abort
 * refactor Player
 * allow swapping trump card
 * ...
 * implement Bezique
 * play with imperfect information
 */
public class App {
    public static void main(String[] args) {
        Schnapsen game = new Schnapsen();
        UctSearch<Schnapsen, SchnapsenState, SchnapsenAction> uct = new UctSearch<>(game);

        List<SchnapsenState> states = new ArrayList<>();
        states.add(game.initializeGame());

        SchnapsenAction selectedAction;
        SchnapsenState lastState = last(states);
        while (!lastState.isTerminal() && states.size() < 50) {
            try {
                selectedAction = uct.getBestAction(lastState);

                states.add(game.getNextState(lastState, selectedAction));
                lastState = last(states);

                if (game.getCurrentPlayer() == PlayerIdentifier.ONE)
                    game.setCurrentPlayer(PlayerIdentifier.TWO);
                else
                    game.setCurrentPlayer(PlayerIdentifier.ONE);

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
