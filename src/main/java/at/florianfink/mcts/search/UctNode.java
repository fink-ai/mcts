package at.florianfink.mcts.search;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.State;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class UctNode<TState extends State, TAction extends Action> {

    @NonNull
    private TState state;
    private final TAction action;
    private final UctNode<TState, TAction> parent;

    private final Set<UctNode<TState, TAction>> children = new HashSet<>();

    private int visitCount = 0;
    private double cumulativeReward = 0;


    // debug values:
    private double expectedReward;
}
