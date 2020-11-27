package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Action;
import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Schnapsen implements Game<SchnapsenState> {
    @Override
    public State initializeGame() {
        SchnapsenState initialState = new SchnapsenState();

        List<Card> deck = Card.generateDeck(2, 3, 4, 10, 11);
        Collections.shuffle(deck);

        for (int i = 0; i < 5; i++) {
            Card top = deck.remove(0);
            initialState.getPlayerOne().getCards().add(top);
            top = deck.remove(0);
            initialState.getPlayerTwo().getCards().add(top);
        }

        initialState.getStockCards().addAll(deck);

        return initialState;
    }

    @Override
    public List<Action> getAllowedActions(SchnapsenState currentState) {
        if (!currentState.getLastTrick().isOpen()) {
            return getActionsForLeadingPlayer(currentState);

        } else {
            return getActionsForRespondingPlayer(currentState);
        }
    }

    private ArrayList<Action> getActionsForRespondingPlayer(SchnapsenState currentState) {
        Player activePlayer = currentState.getActivePlayer();
        ArrayList<Action> actions = new ArrayList<>();
        Card leaderCard = currentState.getLastTrick().getLeaderCard();
        if (currentState.getStockClosedBy() == null && !currentState.getStockCards().isEmpty()) {
            activePlayer.getCards().forEach(card -> actions.add(new SchnapsenAction(card)));
        } else {
            Set<Card> suitableCards = activePlayer.getCards().stream()
                    .filter(card -> card.getSuit() == leaderCard.getSuit())
                    .collect(Collectors.toSet());
            if (suitableCards.isEmpty())
                suitableCards = activePlayer.getCards();

            Set<Card> winningCards = suitableCards.stream()
                    .filter(card -> card.beatsPlayedCard(leaderCard, currentState.getTrumpSuit()))
                    .collect(Collectors.toSet());
            if (!winningCards.isEmpty())
                suitableCards = winningCards;

            suitableCards.forEach(card -> actions.add(new SchnapsenAction(card)));
        }

        return actions;
    }

    private ArrayList<Action> getActionsForLeadingPlayer(SchnapsenState currentState) {
        Player activePlayer = currentState.getActivePlayer();
        ArrayList<SchnapsenAction> nonClosingActions = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();
        activePlayer.getCards().forEach(card -> {
            SchnapsenAction action = new SchnapsenAction();
            action.setPlayCard(card);
            Card.Suit suit = card.getSuit();
            if ((card.getValue() == 3 && activePlayer.getCards().contains(new Card(suit, 4)))
                    || (card.getValue() == 4 && activePlayer.getCards().contains(new Card(suit, 3)))) {
                action.setMeld(new Meld(suit));
            }
            nonClosingActions.add(action);
            actions.add(action);
        });
        if (currentState.getStockClosedBy() == null && currentState.getStockCards().size() > 2) {
            nonClosingActions.forEach(action -> actions.add(action.withCloseStock(true)));
        }
        return actions;
    }

    @Override
    public State getNextState(SchnapsenState currentState, Action action) {
        return null;
    }

    @Override
    public double getReward(SchnapsenState state) {
        assert state.isTerminal();

        int loserScore = state.getScore(state.getOpponent(state.getWinner()));

        if (loserScore == 0) return 3;
        if (loserScore < 33) return 2;
        return 1;

        //TODO: failed closing
    }
}
