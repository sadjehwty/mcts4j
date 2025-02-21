package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.Mcts;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        State state = State.initialize(0);
        Mcts<State, Card, Player> mcts = Mcts.initializeIterations(15000);
        for(int turn=0;!state.isTerminal();turn++) {
            Card action;
            /*if(turn%2==1) action = Card.parse(sc.nextLine());
            else*/ action = mcts.uctSearchWithExploration(state,1.5);
            state.performActionForCurrentAgent(action);
            Game game=state.getCurrentGame();
            if(!state.getCurrentGame().isStarted() && state.getPreviousGame()!=null) {
                Main.printTable(state.getPreviousGame());
                System.out.println("-------------------");
            }
            Main.printTable(game);
            if(!state.isTerminal()){System.out.println("-------------------");}
        }
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
