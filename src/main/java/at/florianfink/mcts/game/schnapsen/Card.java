package at.florianfink.mcts.game.schnapsen;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value &&
                suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, value);
    }

    public boolean beatsPlayedCard(Card playedCard, Suit trumpSuit) {
        return suit == playedCard.suit ?
                value > playedCard.value
                : suit == trumpSuit;
    }
}