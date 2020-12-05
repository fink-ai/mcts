package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.PlayerIdentifier;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class Schnapsen implements Game<SchnapsenState, SchnapsenAction> {

    @Getter
    @Setter
    private PlayerIdentifier currentPlayer = PlayerIdentifier.ONE;

    // TODO: pass to State as starting player?

    @Override
    public SchnapsenState initializeGame() {
        SchnapsenState initialState = new SchnapsenState();

        List<Card> deck = Card.generateDeck(2, 3, 4, 10, 11);
        Collections.shuffle(deck);
        initialState.getStockCards().addAll(deck);
        initialState.setTrumpSuit(deck.get(deck.size() - 1).getSuit());

        for (int i = 0; i < 5; i++) {
            drawCards(initialState);
        }

        exchangeTrumpIfPossible(initialState);
        return initialState;
    }

    @Override
    public Set<SchnapsenAction> getAllowedActions(SchnapsenState currentState) {
        Set<SchnapsenAction> actions;

        if (currentState.isActivePlayerLeading()) {
            actions = getActionsForLeadingPlayer(currentState);

        } else {
            actions = getActionsForRespondingPlayer(currentState);
        }

        if (actions.isEmpty())
            throw new IllegalStateException("There should always be at least one action available");

        return actions;
    }

    private HashSet<SchnapsenAction> getActionsForRespondingPlayer(SchnapsenState currentState) {
        Player activePlayer = currentState.getPlayerByIdentifier(currentState.getActivePlayer()); // TODO: refactor
        HashSet<SchnapsenAction> actions = new HashSet<>();
        Card leaderCard = currentState.getLastTrick().getLeaderCard();
        if (currentState.isStockAvailable()) {
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

    private HashSet<SchnapsenAction> getActionsForLeadingPlayer(SchnapsenState currentState) {
        Player activePlayer = currentState.getPlayerByIdentifier(currentState.getActivePlayer());
        ArrayList<SchnapsenAction> nonClosingActions = new ArrayList<>();
        HashSet<SchnapsenAction> actions = new HashSet<>();
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
    public SchnapsenState getNextState(SchnapsenState currentState, SchnapsenAction action) {
        SchnapsenState newState = new SchnapsenState(currentState);

        PlayerIdentifier previousPlayer = currentState.getActivePlayer();
        PlayerIdentifier previousOpponent = newState.getOpponent(previousPlayer);

        ArrayList<Trick> history = newState.getHistory();
        if (newState.isActivePlayerLeading()) {
            newState.setActivePlayer(previousOpponent);
            history.add(new Trick(
                    previousPlayer,
                    previousOpponent,
                    null,
                    action.getPlayCard(),
                    null,
                    action.getMeld(),
                    action.isCloseStock()
            ));
            if (action.isCloseStock()) {
                newState.setStockClosedBy(previousPlayer);
                int opponentScore = newState.getScore(previousOpponent);
                newState.setOpponentScoreAtStockClosing(opponentScore);
            }
        } else {
            Trick lastTrick = newState.getLastTrick();
            Trick completedTrick = lastTrick.toBuilder()
                    .responderCard(action.getPlayCard())
                    .winner(action.getPlayCard().beatsPlayedCard(lastTrick.getLeaderCard(), newState.getTrumpSuit())
                            ? previousPlayer
                            : previousOpponent
                    )
                    .build();
            history.remove(history.size() - 1);
            history.add(completedTrick);

            newState.setActivePlayer(completedTrick.getWinner());

            if (newState.isStockAvailable()) {
                drawCards(newState);
            }
        }


        newState.getPlayerByIdentifier(previousPlayer).getCards().remove(action.getPlayCard()); // TODO: should happen before drawing?
        exchangeTrumpIfPossible(newState);
        return newState;
    }

    private void exchangeTrumpIfPossible(SchnapsenState state) {
        if (!state.isActivePlayerLeading()
                || state.getStockClosedBy() != null
                || state.getStockCards().size() <= 2
        ) return;

        Player activePlayer = state.getPlayerByIdentifier(state.getActivePlayer());
        Card trumpTwo = new Card(state.getTrumpSuit(), 2);
        ArrayList<Card> stockCards = state.getStockCards();
        if (activePlayer.getCards().remove(trumpTwo)) {
            activePlayer.getCards().add(stockCards.remove(stockCards.size() - 1));
            stockCards.add(trumpTwo);
        }
    }

    @Override
    public double getReward(SchnapsenState state) {
        assert state.isTerminal();

        double absoluteReward = getAbsoluteReward(state);

        return state.getWinner() == currentPlayer
                ? absoluteReward
                : -absoluteReward;
    }

    public double getAbsoluteReward(SchnapsenState state) {
        PlayerIdentifier loser = state.getOpponent(state.getWinner());

        if (state.getStockClosedBy() == loser) {
            if (state.getOpponentScoreAtStockClosing() == 0) {
                return 3;
            }
            return 2;
        }

        int loserScore = state.getScore(loser);
        if (loserScore == 0) return 3;
        if (loserScore < 33) return 2;

        return 1;
    }

    private void drawCards(SchnapsenState state) {
        Card top = state.getStockCards().remove(0);
        state.getPlayerOne().getCards().add(top);
        top = state.getStockCards().remove(0);
        state.getPlayerTwo().getCards().add(top);
    }
}
