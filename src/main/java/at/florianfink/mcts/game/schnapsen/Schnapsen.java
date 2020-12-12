package at.florianfink.mcts.game.schnapsen;

import at.florianfink.mcts.game.Game;
import at.florianfink.mcts.game.PlayerIdentifier;

import java.util.*;
import java.util.stream.Collectors;

import static at.florianfink.mcts.game.PlayerIdentifier.ONE;
import static at.florianfink.mcts.game.PlayerIdentifier.TWO;

public class Schnapsen implements Game<SchnapsenState, SchnapsenAction> {

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

        initializeHiddenCards(initialState, ONE);
        initializeHiddenCards(initialState, TWO);

        exchangeTrumpIfPossible(initialState);
        return initialState;
    }

    private void initializeHiddenCards(SchnapsenState state, PlayerIdentifier player) {
        state.getHiddenCards(player).addAll(state.getCards(player.getOpponent()));
        state.getHiddenCards(player).addAll(state.getStockCards());
        state.getHiddenCards(player).remove(state.getStockCards().get(state.getStockCards().size() - 1));
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
        PlayerIdentifier activePlayer = currentState.getActivePlayer();
        HashSet<SchnapsenAction> actions = new HashSet<>();
        Card leaderCard = currentState.getLastTrick().getLeaderCard();
        if (currentState.isStockAvailable()) {
            currentState.getCards(activePlayer).forEach(card -> actions.add(new SchnapsenAction(card)));
        } else {
            Set<Card> suitableCards = currentState.getCards(activePlayer).stream()
                    .filter(card -> card.getSuit() == leaderCard.getSuit())
                    .collect(Collectors.toSet());
            if (suitableCards.isEmpty())
                suitableCards = currentState.getCards(activePlayer);

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
        PlayerIdentifier activePlayer = currentState.getActivePlayer();
        ArrayList<SchnapsenAction> nonClosingActions = new ArrayList<>();
        HashSet<SchnapsenAction> actions = new HashSet<>();
        currentState.getCards(activePlayer).forEach(card -> {
            SchnapsenAction action = new SchnapsenAction();
            action.setPlayCard(card);
            Card.Suit suit = card.getSuit();
            if ((card.getValue() == 3 && currentState.getCards(activePlayer).contains(new Card(suit, 4)))
                    || (card.getValue() == 4 && currentState.getCards(activePlayer).contains(new Card(suit, 3)))) {
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
        PlayerIdentifier previousOpponent = previousPlayer.getOpponent();

        List<Trick> history = newState.getHistory();
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
            // TODO: remove meld card from hiddenCards
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


        newState.getCards(previousPlayer).remove(action.getPlayCard());
        newState.getHiddenCards(previousOpponent).remove(action.getPlayCard());

        exchangeTrumpIfPossible(newState);
        return newState;
    }

    private void exchangeTrumpIfPossible(SchnapsenState state) {
        if (!state.isActivePlayerLeading()
                || state.getStockClosedBy() != null
                || state.getStockCards().size() <= 2
        ) return;

        PlayerIdentifier activePlayer = state.getActivePlayer();
        Card trumpTwo = new Card(state.getTrumpSuit(), 2);
        ArrayList<Card> stockCards = state.getStockCards();
        if (state.getCards(activePlayer).remove(trumpTwo)) {
            state.getCards(activePlayer).add(stockCards.remove(stockCards.size() - 1));
            stockCards.add(trumpTwo);
            state.getHiddenCards(activePlayer.getOpponent()).remove(trumpTwo);
        }
    }

    @Override
    public double getReward(SchnapsenState state) {
        assert state.isTerminal();

        PlayerIdentifier loser = state.getWinner().getOpponent();

        if (loser.equals(state.getStockClosedBy())) {
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
        state.getCards(ONE).add(top);
        state.getHiddenCards(ONE).remove(top);
        top = state.getStockCards().remove(0);
        state.getCards(TWO).add(top);
        state.getHiddenCards(TWO).remove(top);
    }

    @Override
    public SchnapsenState determinizeHiddenInformation(SchnapsenState state, Random random) {
        // TODO IncInf: don't create copy, instead randomize given state
        SchnapsenState randomizedState = new SchnapsenState(state);
        ArrayList<Card> hiddenCards = new ArrayList<>(state.getHiddenCards(state.getActivePlayer()));

        Set<Card> newOpponentCards = randomizedState.getCards(state.getActivePlayer().getOpponent());
        newOpponentCards.removeIf(hiddenCards::contains);
        randomizedState.getStockCards().removeIf(hiddenCards::contains);

        Collections.shuffle(hiddenCards, random);

        for (int i = newOpponentCards.size(); i < state.getCards(state.getActivePlayer().getOpponent()).size(); i++) {
            newOpponentCards.add(hiddenCards.remove(0));
        }
        for (int i = randomizedState.getStockCards().size(); i < state.getStockCards().size(); i++) {
            randomizedState.getStockCards().add(0, hiddenCards.remove(0));
        }

        assert hiddenCards.isEmpty();

        return randomizedState;
    }
}
