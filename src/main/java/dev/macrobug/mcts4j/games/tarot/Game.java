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
    public Card[] getCards(){
        return new Card[]{
                cards[0],
                cards[1],
                cards[2],
                cards[3]
        };
    }
    public void setCurrent(Card c){
        for(int i=0;i<cards.length;i++)
            if(cards[i]==null){
                cards[i]=c;
                break;
            }
    }
    public int getTurn(){
        int delta = firstPlayerIndex;
        for(int i=0;i<3;i++)
            if(cards[i]!=null)
                delta++;
        return delta%4;
    }

    public Game(int firstPlayerIndex){
        this(firstPlayerIndex,new Card[4]);
    }
    public Game(int firstPlayerIndex, Card[] cards){
        this.firstPlayerIndex=firstPlayerIndex;
        this.cards=cards;
    }
}