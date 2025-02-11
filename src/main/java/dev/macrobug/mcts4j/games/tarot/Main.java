package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.Mcts;

import java.util.Scanner;

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
            Card[] cards=state.getCurrentGame().getCards();
            for(int i=3;i>=0;i--){
                Card card=cards[i];
                StringBuffer stringBuffer=new StringBuffer("         ");
                int start=switch (i){
                    case 1->0;
                    case 2->6;
                    default->3;
                };
                if(card!=null)
                    stringBuffer.replace(start,start+3, card.toString());
                System.out.println(stringBuffer);
                System.out.println();
            }
            if(!state.isTerminal()){System.out.println("-------------------");}
        }
    }
}
