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

    // TODO modificarlo per rendere l'informazione incompleta
    @Override
    public List<Card> getAvailableActionsForCurrentAgent() {
        Game currentGame = games.getLast();
        Player currentPlayer = getCurrentAgent();
        List<Card> availableActions = currentPlayer.getDeck();
        if(currentGame.firstPlayerIndex()!=getCurrentIndex()){
            if(availableActions.stream().anyMatch((c)->c.suit().equals(currentGame.semeDiMano())))
                availableActions=availableActions.stream().filter((c)->c.suit().equals(currentGame.semeDiMano())).toList();
            else if (availableActions.stream().anyMatch((c)->c.suit().equals(Suit.TRIONFI)))
                availableActions=availableActions.stream().filter((c)->c.suit().equals(Suit.TRIONFI)).toList();
        }
        return availableActions;
    }

    @Override
    public State performActionForCurrentAgent(Card card) {
        validateIsValidAction(card);
        applyActionOnBoard(card);
        return this;
    }
    private void validateIsValidAction(Card action) {
        if (!getAvailableActionsForCurrentAgent().contains(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private void applyActionOnBoard(Card card) {
        Game currentGame = games.getLast();
        Player currentPlayer = getCurrentAgent();
        currentPlayer.getDeck().remove(card);
        currentPlayer.getGone().add(card);
        if(currentGame.first()==null) currentGame.first=card;
        else if (currentGame.second()==null) currentGame.second=card;
        else if(currentGame.third()==null) currentGame.third=card;
        else {
            currentGame.fourth=card;
            games.add(generateNextGame(currentGame));
        }
    }
    private Game generateNextGame(Game current){
        boolean trionfo=false;
        Card[] cards = new Card[]{
                current.first(),
                current.second(),
                current.third(),
                current.fourth()
        };

        Card matto = new Card(0,Suit.TRIONFI);
        int delta=0;
        Card max = cards[0];
        int indiceMatto=-1;
        List<Card> presa=new ArrayList<>();
        for(int i=0;i<4;i++){
            Card card=cards[i];
            if(card.equals(matto)) {
                indiceMatto=i;
            }else {
                presa.add(card);
                if (trionfo) {
                    if(card.suit().equals(Suit.TRIONFI) && max.compareTo(card)<0){
                        delta = i;
                        max = card;
                    }
                } else {
                    if (card.suit().equals(Suit.TRIONFI)) {
                        trionfo = true;
                        delta = i;
                        max = card;
                    } else if (card.suit().equals(current.semeDiMano()) && max.compareTo(card)<0) {
                        delta = i;
                        max = card;
                    }
                }
            }
        }
        if(indiceMatto>=0) {
            if ((indiceMatto + current.firstPlayerIndex()) % 2 == 0)
                preseNS.add(matto);
            else
                preseEW.add(matto);
        }
        if ((delta + current.firstPlayerIndex()) % 2 == 0)
            preseNS.addAll(presa);
        else
            preseEW.addAll(presa);
        return new Game(null, (delta + current.firstPlayerIndex()) % 4,null,null,null,null);
    }

    // TODO fino qui

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
