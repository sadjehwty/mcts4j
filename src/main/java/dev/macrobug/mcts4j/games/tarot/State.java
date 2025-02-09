package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.MctsDomainState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Card, Player> {
    private ArrayList<Card> preseNS= new ArrayList<>();
    private ArrayList<Card> preseEW= new ArrayList<>();
    private ArrayList<Game> games = new ArrayList<>();
    private final Player[] players;
    private static final ArrayList<Card> allCards = initializeDeck();

    public static State initialize(int firstPlayer) {
        Player[] players = initializePlayers(firstPlayer);
        return new State(new Game(null,(firstPlayer+1)%4,null,null, null,null), players);
    }

    private State(Game game, Player[] players) {
        this.players = players;
        games.add(game);
    }

    private static ArrayList<Card> initializeDeck() {
        ArrayList<Card> deck= new ArrayList<>();
        for(Suit suit: Suit.values()){
            if(suit==Suit.TRIONFI){
                deck.add(new Card(0,suit));
                deck.add(new Card(1,suit));
                for(int i=0;i<4;i++)
                    deck.add(new Card(4,suit));
                for(int i=5;i<22;i++)
                    deck.add(new Card(i,suit));
            }else{
                deck.add(new Card(1,suit));
                for(int i=6;i<15;i++)
                    deck.add(new Card(i,suit));
            }
        }
        return deck;
    }

    private static Player[] initializePlayers(int firstPlayer) {
        Player[] players = new Player[4];
        Random rand = new Random(System.currentTimeMillis());
        List<Card> temp = new ArrayList<>(State.allCards.size());
        temp.addAll(State.allCards);
        temp = temp.stream().sorted((a,b)-> Double.compare(rand.nextDouble(), 0.5)).collect(Collectors.toList());
        int delta = 3-firstPlayer;
        for(int i=0;i<4;i++){
            players[(i+delta)%4] = Player.create(temp.subList(i*15,i<3?15:17));
        }
        return players;
    }

    @Override
    public boolean isTerminal() {
        return preseEW.size()+preseNS.size()==62;
    }

    private int getCurrentIndex(){
        Game current = games.getLast();
        int delta = current.firstPlayerIndex();
        if(current.first()!=null) delta++;
        if(current.second()!=null) delta++;
        if(current.third()!=null) delta++;
        return delta%4;
    }
    @Override
    public Player getCurrentAgent() {
        return players[getCurrentIndex()];
    }

    @Override
    public Player getPreviousAgent() {
        Game current = games.getLast();
        if(current.first()==null) {
            current = games.get(games.size() - 2);
            return players[(current.firstPlayerIndex()+3)%4];
        }else {
            int currentIndex=getCurrentIndex();
            if(currentIndex==0) currentIndex=4;
            return players[currentIndex- 1];
        }
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

 // TODO fino qui
    @Override
    public List<Card> getAvailableActionsForCurrentAgent() {
        List<String> availableActions = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            List<String> availableActionsInRow = getAvailableActionsInBoardRow(board[row], row);
            availableActions.addAll(availableActionsInRow);
        }
        return availableActions;
    }

    @Override
    public State performActionForCurrentAgent(Card card) {
        return null;
    }

    private List<String> getAvailableActionsInBoardRow(char[] row, int rowIndex) {
        List<String> availableActionsInRow = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < BOARD_SIZE; columnIndex++) {
            if (row[columnIndex] == EMPTY_BOARD_POSITION) {
                String action = generateActionFromBoardPosition(rowIndex, columnIndex);
                availableActionsInRow.add(action);
            }
        }
        return availableActionsInRow;
    }

    private String generateActionFromBoardPosition(int row, int column) {
        return Integer.toString(row) + Integer.toString(column);
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(String action) {
        validateIsValidAction(action);
        applyActionOnBoard(action);
        selectNextPlayer();
        currentRound++;
        return this;
    }

    private void validateIsValidAction(String action) {
        if (!getAvailableActionsForCurrentAgent().contains(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private void applyActionOnBoard(String action) {
        int row = getRowFromAction(action);
        int column = getColumnFromAction(action);
        board[row][column] = players[currentPlayerIndex].getBoardPositionMarker();
    }

    protected MctsDomainState undoAction(String action) {
        validateIsValidUndoAction(action);
        applyUndoActionOnBoard(action);
        selectNextPlayer();
        currentRound--;
        return this;
    }

    private void validateIsValidUndoAction(String action) {
        int row = getRowFromAction(action);
        int column = getColumnFromAction(action);
        if (!(-1 < row && row < 3) && !(-1 < column && column < 3))
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
    }

    private void applyUndoActionOnBoard(String action) {
        int row = getRowFromAction(action);
        int column = getColumnFromAction(action);
        board[row][column] = EMPTY_BOARD_POSITION;
    }

    private int getRowFromAction(String action) {
        String row = action.split("")[ACTION_ROW_POSITION];
        return Integer.parseInt(row);
    }

    private int getColumnFromAction(String action) {
        String column = action.split("")[ACTION_COLUMN_POSITION];
        return Integer.parseInt(column);
    }

    private void selectNextPlayer() {
        currentPlayerIndex = 2 - currentPlayerIndex - 1;
        previousPlayerIndex = 2 - previousPlayerIndex - 1;
    }

    @Override
    public MctsDomainState skipCurrentAgent() {
        return this;
    }
}
