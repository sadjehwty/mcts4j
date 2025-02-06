package dev.macrobug.mcts4j.games.tarot;
import io.github.nejc92.mcts.MctsDomainAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player implements MctsDomainAgent<State> {
    private ArrayList<State.Card> deck;
    private final ArrayList<State.Card> gone=new ArrayList<>();

    public Player(ArrayList<State.Card> deck) {
        this.deck=deck;
    }
    public ArrayList<State.Card> getGone(){
        return gone;
    }

    @Override
    public State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            String action = getBiasedOrRandomActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(action);
        }
        return state;
    }

    private String getBiasedOrRandomActionFromStatesAvailableActions(State state) {
        List<String> availableActions = state.getAvailableActionsForCurrentAgent();
        for (String action : availableActions) {
            if (actionWinsGame(state, action))
                return action;
        }
        return getRandomActionFromActions(availableActions);
    }

    private boolean actionWinsGame(State state, String action) {
        state.performActionForCurrentAgent(action);
        boolean actionEndsGame = state.isTerminal();
        state.undoAction(action);
        return actionEndsGame;
    }

    private String getRandomActionFromActions(List<String> actions) {
        Collections.shuffle(actions);
        return actions.get(0);
    }

    @Override
    public double getRewardFromTerminalState(State terminalState) {
        if (terminalState.specificPlayerWon(this))
            return 1;
        else if (terminalState.isDraw())
            return 0.5;
        else
            return 0;
    }
}