package dev.macrobug.mcts4j.games.tarot;

public class Card implements Comparable<Card>{
    public final static Card MATTO=new Card(0,Suit.TRIONFI);
    public final static Card BEGATTO=new Card(1,Suit.TRIONFI);
    public final static Card MORETTO=new Card(4,Suit.TRIONFI);
    public final static Card MONDO=new Card(19,Suit.TRIONFI);
    public final static Card ANGELO=new Card(20,Suit.TRIONFI);


    private final int value;
    private final Suit suit;

    public static Card parse(String s) {
        return new Card(Integer.parseInt(s.substring(0,2)),Suit.parse(s.substring(2)));
    }

    public Suit suit(){return suit;}
    public int value(){return value;}

    public Card(int value, Suit suit){
        this.value=value;
        this.suit=suit;
    }
    public boolean equals(Object o){
        return o instanceof Card c && c.suit.equals(suit) && c.value == value;
    }
    @Override
    public int compareTo(Card card) {
        if(suit.equals(card.suit)){
            return switch (suit){
                case TRIONFI -> value>=card.value?1:-1;
                case COPPE, DENARI ->value<11 && card.value<11 ? 1-Integer.compare(value,card.value) :Integer.compare(value,card.value);
                case BASTONI, SPADE ->Integer.compare(value,card.value);
            };
        }else if(suit.equals(Suit.TRIONFI) || !card.suit.equals(Suit.TRIONFI)) return 1;
        return -1;
    }

    @Override
    public String toString() {
        return String.format("%2d%c",value,suit.toString().toCharArray()[0]);
    }
}
