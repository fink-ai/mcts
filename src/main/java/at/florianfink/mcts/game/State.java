package at.florianfink.mcts.game;

public interface State {
    boolean isTerminal();

    PlayerIdentifier getActivePlayer();

    PlayerIdentifier getWinner();


}
