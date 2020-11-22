package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

@Data
public class Card {

    enum Suit {
        HEART,
        DIAMOND,
        SPADE,
        CLUB
    }

    private final Suit suit;
    private final int value;
}