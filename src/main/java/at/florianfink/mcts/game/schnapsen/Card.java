package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<Card> generateDeck(int... values) {
        ArrayList<Card> deck = new ArrayList<>();
        Arrays.stream(values).forEach(value ->
                Arrays.stream(Suit.values()).forEach(suit ->
                        deck.add(new Card(suit, value))
                )
        );

        return deck;
    }
}