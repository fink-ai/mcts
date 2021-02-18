package at.florianfink.mcts;

import at.florianfink.mcts.game.schnapsen.Schnapsen;
import at.florianfink.mcts.game.schnapsen.SchnapsenAction;
import at.florianfink.mcts.game.schnapsen.SchnapsenState;
import at.florianfink.mcts.search.UctNode;
import at.florianfink.mcts.search.UctSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO:
 * if only one action, immediately select without search
 * don't throw away tree after each call to uct
 * Schnapsen can end with meld
 * if stock closed: infer opponent cards from response (no cards of suit, no trump cards)
 * ...
 * implement Bezique
 * ...
 * multiple threads
 */
public class App {
    public static void main(String[] args) {
        Schnapsen game = new Schnapsen();
        Random rand = new Random(432573);
        UctSearch<Schnapsen, SchnapsenState, SchnapsenAction> uct = new UctSearch<>(game, rand);
        ArrayList<UctNode<SchnapsenState, SchnapsenAction>> roots = new ArrayList<>();

        List<SchnapsenState> states = new ArrayList<>();
        states.add(game.initializeGame(rand));

        SchnapsenAction selectedAction;
        SchnapsenState lastState = last(states);
        while (!lastState.isTerminal() && states.size() < 50) {
            try {
                selectedAction = uct.getBestAction(lastState);
                roots.add(uct.getRoot());

                states.add(game.getNextState(lastState, selectedAction));
                lastState = last(states);

                System.out.println("next move starting ...");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        System.out.println("hooray it didn't crash! " + roots.size());
    }

    private static SchnapsenState last(List<SchnapsenState> list) {
        return list.get(list.size() - 1);
    }


}
