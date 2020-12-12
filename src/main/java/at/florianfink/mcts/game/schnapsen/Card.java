package at.florianfink.mcts.game.schnapsen;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
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

    public boolean beatsPlayedCard(Card playedCard, Suit trumpSuit) {
        return suit == playedCard.suit
                ? value > playedCard.value
                : suit == trumpSuit;
    }
}