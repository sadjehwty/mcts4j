package dev.macrobug.mcts4j.games.tarot;

public enum Suit{
    SPADE,
    COPPE,
    DENARI,
    BASTONI,
    TRIONFI;

    public String toString(){
        return this.name().toUpperCase().substring(0,1);
    }
}
