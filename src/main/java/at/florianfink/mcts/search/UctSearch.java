package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UctSearch<TGame extends Game<TState, TAction>, TState extends State, TAction extends Action> {

    private final TGame game;
    @Setter
    private long secondsToSearch = 10;
    @Setter
    private Evaluator<TGame, TState, TAction> evaluator = new RandomPolicyEvaluator<>();

    @Setter
    private Random rand = new Random();

    private UctNode<TState, TAction> root;

    public TAction getBestAction(TState state) {
        root = new UctNode<>(state, null, null);

        for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(secondsToSearch);
             stop > System.nanoTime(); ) { // TODO: there should be a better way to limit the time

            UctNode<TState, TAction> nextChild = treePolicy(root);
            double reward = evaluator.getReward(game, nextChild.getState());
            backupReward(nextChild, reward);
        }

        return getBestChild(root, 0).getAction();
    }

    public UctNode<TState, TAction> treePolicy(UctNode<TState, TAction> node) {
        while (!node.getState().isTerminal()) {
            if (node.getUntriedActions() == null) {
                node.setUntriedActions(game.getAllowedActions(node.getState()));
            }

            if (node.getUntriedActions().isEmpty()) {
                node = getBestChild(node, 0.7);
            } else {
                node = expandNode(node);
            }
        }

        return node;
    }

    public UctNode<TState, TAction> getBestChild(UctNode<TState, TAction> node, double c) {
        return node.getChildren().stream()
                .max(Comparator.comparing(child -> ucb(child, c))).orElse(null);
    }

    private double ucb(UctNode<TState, TAction> node, double c) { // TODO: make injectable
        double exploitation = node.getCumulativeReward() / node.getVisitCount();
        double exploration = Math.sqrt(2 * Math.log(node.getParent().getVisitCount()) / node.getVisitCount());

        return exploitation + c * exploration;
    }

    public UctNode<TState, TAction> expandNode(UctNode<TState, TAction> node) {
        TAction randomAction = node.getUntriedActions().stream()
                .skip(rand.nextInt(node.getUntriedActions().size())).findFirst().orElse(null);

        UctNode<TState, TAction> newChild = new UctNode<>(
                game.getNextState(node.getState(), randomAction),
                randomAction,
                node
        );
        node.getUntriedActions().remove(randomAction);
        node.getChildren().add(newChild);

        return newChild;
    }

    public void backupReward(UctNode<TState, TAction> node, double reward) {
        while (node != null) {
            node.setVisitCount(node.getVisitCount() + 1);
            node.setCumulativeReward(node.getCumulativeReward() + reward);

            node.setExpectedReward(node.getCumulativeReward() / node.getVisitCount());

            reward = -reward; // parent node is opponent
            node = node.getParent();
        }
    }

}
