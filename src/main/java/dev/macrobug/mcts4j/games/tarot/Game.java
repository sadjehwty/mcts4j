package dev.macrobug.mcts4j.games.tarot;

public class Game{
    private final int firstPlayerIndex;
    private final Card[] cards;

    public Suit semeDiMano(){
        return isStarted()?cards[0].suit():null;
    }
    public int getFirstPlayerIndex(){return firstPlayerIndex;}
    public boolean isStarted(){return cards[0]!=null;}
    public boolean isDone(){return cards[3]!=null;}

    public Game(int firstPlayerIndex){
        this.firstPlayerIndex=firstPlayerIndex;
        this.cards=new Card[4];
    }
}