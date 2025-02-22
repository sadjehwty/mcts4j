package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.Mcts;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Mcts<State, Card, Player> mcts = Mcts.initializeIterations(15000);
        int ns=0,ew=0;
        for(int i=0;i<4;i++) {
            State state = State.initialize(i);
            for (int turn = 0; !state.isTerminal(); turn++) {
                Card action;
                if(((state.getCurrentGame().getFirstPlayerIndex()+turn)%4)==0){
                    ArrayList<Card> t = new ArrayList<>(state.getCurrentAgent().getDeck());
                    t.sort((c1,c2)->c1.suit().compareTo(c2.suit())!=0 ? c1.suit().compareTo(c2.suit()) : Integer.compare(c1.value(),c2.value()));
                    boolean good=true;
                    int choise=-1;
                    do{
                        for(int j=0;j<t.size();j++)
                            System.out.print(String.format("%1$4d",j));
                        System.out.println();
                        System.out.println(t);
                        choise = Integer.parseInt(sc.nextLine());
                        good = choise < t.size() && choise>-1;
                    }while(!good);
                    action = t.get(choise);
                } else
                    action = mcts.uctSearchWithExploration(state, 1.5);
                state.performActionForCurrentAgent(action);
                Game game = state.getCurrentGame();
                if (!state.getCurrentGame().isStarted() && state.getPreviousGame() != null) {
                    Main.printTable(state.getPreviousGame());
                    System.out.println("-------------------");
                }
                Main.printTable(game);
                if (!state.isTerminal()) {
                    System.out.println("-------------------");
                }
            }
            int t = State.getPoints(state.getPreseNS(), state.getCurrentGame());
            ns+=t;
            System.out.println("NS: " + t);
            t=State.getPoints(state.getPreseEW(), state.getCurrentGame());
            ew+=t;
            System.out.println("EW: " + t);
        }
        System.out.println("===================");
        System.out.println("NS: " + ns);
        System.out.println("EW: " + ew);
    }

    private static void printTable(Game game) {
        Card[] cards = game.getCards();
        int[] ids=new int[]{2,1,3,0};
        for(int i:ids){
            int j = 4-i-game.getFirstPlayerIndex();
            if(j<0) j+=4;
            if(j>3) j-=4;
            Card card=cards[j];
            StringBuffer stringBuffer=new StringBuffer("         ");
            int start=switch (i){
                case 1->0;
                case 3->6;
                default->3;
            };
            if(card!=null)
                stringBuffer.replace(start,start+3, card.toString());
            System.out.println(stringBuffer);
        }
    }
}
