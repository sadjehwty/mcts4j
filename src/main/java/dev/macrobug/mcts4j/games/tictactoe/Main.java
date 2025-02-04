package dev.macrobug.mcts4j.games.tictactoe;

import io.github.nejc92.mcts.Mcts;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TicTacToeState state = TicTacToeState.initialize(TicTacToePlayer.Type.NOUGHT);
        Mcts<TicTacToeState, String, TicTacToePlayer> mcts = Mcts.initializeIterations(15000);
        for(int turn=0;!state.isTerminal();turn++) {
            String action;
            if(turn%2==1) action = sc.nextLine();
            else action = mcts.uctSearchWithExploration(state,1.5);
            state.performActionForCurrentAgent(action);
            char[][] board=state.getBoard();
            for (char[] chars : board) {
                for (char aChar : chars) {
                    System.out.print(aChar + " ");
                }
                System.out.println();
            }
            if(state.isDraw()){System.out.println("===================");}
            else if(!state.isTerminal()){System.out.println("-------------------");}
        }
    }
}
