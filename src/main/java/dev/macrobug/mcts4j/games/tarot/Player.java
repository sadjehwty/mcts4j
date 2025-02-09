package dev.macrobug.mcts4j.games.tarot;
import io.github.nejc92.mcts.MctsDomainAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player implements MctsDomainAgent<State> {
    private final int position;
    private final ArrayList<Card> deck;
    private final ArrayList<Card> gone=new ArrayList<>();

    public Player(ArrayList<Card> deck,int position) {
        this.deck=deck;
        this.position = position;
    }
    public ArrayList<Card> getGone(){
        return gone;
    }
    public ArrayList<Card> getDeck(){return deck;}

    @Override
    public State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            Card action = getBiasedOrRandomActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(action);
        }
        return state;
    }

    private Card getBiasedOrRandomActionFromStatesAvailableActions(State state) {
        List<Card> availableActions = state.getAvailableActionsForCurrentAgent();
        for (Card action : availableActions) {
            if (actionWinsGame(state, action))
                return action;
        }
        return getRandomActionFromActions(availableActions);
    }

    private boolean actionWinsGame(State state, Card action) {
        state.performActionForCurrentAgent(action);
        boolean actionEndsGame = state.isTerminal();
        state.undoAction(action);
        return actionEndsGame;
    }

    private Card getRandomActionFromActions(List<Card> actions) {
        Random rand = new Random(System.currentTimeMillis());
        return actions.stream().sorted((_, _)-> Double.compare(rand.nextDouble(), 0.5)).toList().getFirst();
    }

    @Override
    public double getRewardFromTerminalState(State terminalState) {
        return (position %2==0 ? 1 : -1) * State.getPoints(terminalState.getPreseNS())-State.getPoints(terminalState.getPreseEW());
    }
}