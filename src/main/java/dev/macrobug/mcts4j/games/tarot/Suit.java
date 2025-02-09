package dev.macrobug.mcts4j.games.tarot;

public enum Suit{
    SPADE,
    COPPE,
    DENARI,
    BASTONI,
    TRIONFI;

    public static Suit parse(String substring) {
        return switch (substring.toUpperCase().toCharArray()[0]){
            case 'S'->SPADE;
            case 'C'->COPPE;
            case 'D'->DENARI;
            case 'B'->BASTONI;
            case 'T'->TRIONFI;
            default -> throw new RuntimeException();
        };
    }
    public String toString(){
        return this.name().toUpperCase().substring(0,1);
    }
}
