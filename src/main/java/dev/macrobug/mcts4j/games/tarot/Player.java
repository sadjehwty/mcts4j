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

    public List<Card> scartataIniziale() {
        ArrayList<Card> carte=getDeck();
        record Statistica(boolean capo, int count, Card ultima, Card penultima) implements Comparable<Statistica>{
            @Override
            public int compareTo(Statistica s) {
                if(!this.capo && this.count==1 && !(!s.capo && s.count==1)) return -1;
                if(!this.capo && this.count==2 && (s.capo || s.count>2)) return -1;
                if(!this.capo && s.capo) return -1;
                if(!s.capo && s.count==1 && !(!this.capo && this.count==1)) return 1;
                if(!s.capo && s.count==2 && (this.capo || this.count>2)) return 1;
                if(!s.capo && this.capo) return 1;
                return 1-Integer.compare(this.count,s.count);
            }
        }
        List<Statistica> carteStatistica = new ArrayList<>();
        List<Card> scartate = new ArrayList<>();
        for(Suit suit: Suit.values()){
            int capo=suit.equals(Suit.TRIONFI)?20:14;
            List<Card> cards = carte.stream().filter((c)->c.suit().equals(suit) && !c.equals(Card.MATTO) && !c.equals(Card.BEGATTO)).sorted().toList();
            if(!cards.isEmpty())
                carteStatistica.add(new Statistica(cards.contains(new Card(capo,suit)),cards.size(), cards.get(0),cards.size()>1?cards.get(1):null));
        }
        ArrayList<Statistica> singole=new ArrayList<>(carteStatistica.stream().sorted().toList());
        while(scartate.size()<2){
            Statistica s = singole.removeFirst();
            if(s.ultima!=null &&
                    !s.ultima.equals(Card.MATTO) &&
                    !s.ultima.equals(Card.BEGATTO) &&
                    !s.ultima.equals(Card.MONDO) &&
                    !s.ultima.equals(Card.ANGELO) && (
                    s.ultima.suit().equals(Suit.TRIONFI) ||
                            s.ultima.value()<14
            )){
                scartate.add(s.ultima);
                getDeck().remove(s.ultima);
            }
            if(scartate.size()==2) continue;
            if(s.penultima!=null &&
                    !s.penultima.equals(Card.MATTO) &&
                    !s.penultima.equals(Card.BEGATTO) &&
                    !s.penultima.equals(Card.MONDO) &&
                    !s.penultima.equals(Card.ANGELO) && (
                    s.penultima.suit().equals(Suit.TRIONFI) ||
                            s.penultima.value()<14
            )){
                scartate.add(s.penultima);
                getDeck().remove(s.penultima);
            }
        }
        return scartate;
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
        Game game=terminalState.getCurrentGame();
        return (position %2==0 ? 1 : -1) * State.getPoints(terminalState.getPreseNS(),game)-State.getPoints(terminalState.getPreseEW(),game);
    }
}