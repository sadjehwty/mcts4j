package dev.macrobug.mcts4j.games.tarot;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class StateTest {
    @Test
    public void getPointsTest() {
        // NESSUNA PRESA
        assertEquals(0, State.getPoints(new ArrayList<>(),new Game(0)));
        // CAPPOTTO
        assertEquals(0, State.getPoints(new ArrayList<>(List.of(Card.MATTO)),new Game(0)));
        ArrayList<Card> cards = new ArrayList<>(State.allCards);
        Game game=new Game(0,new Card[]{Card.MORETTO,Card.MORETTO,Card.MORETTO,Card.MORETTO});
        assertEquals(978, State.getPoints(cards,game));
        cards.remove(Card.MATTO);
        assertEquals(978, State.getPoints(cards,game));
    }

    @Test
    public void scartataIniziale() {
        Player player = new Player(new ArrayList<>(List.of(
                new Card(14,Suit.BASTONI),
                new Card(1,Suit.BASTONI),
                new Card(10,Suit.COPPE),
                new Card(9,Suit.COPPE),
                new Card(8,Suit.SPADE),
                new Card(13,Suit.DENARI),
                new Card(19,Suit.TRIONFI),
                new Card(18,Suit.TRIONFI),
                new Card(17,Suit.TRIONFI),
                new Card(16,Suit.TRIONFI),
                new Card(15,Suit.TRIONFI),
                new Card(14,Suit.TRIONFI),
                new Card(13,Suit.TRIONFI),
                new Card(12,Suit.TRIONFI),
                new Card(2,Suit.TRIONFI),
                new Card(1,Suit.TRIONFI),
                new Card(0,Suit.TRIONFI))),0);
        assertArrayEquals(new ArrayList<>(List.of(
                new Card(8,Suit.SPADE),
                new Card(13,Suit.DENARI)
        )).toArray(), State.scartataIniziale(player).toArray());

        player = new Player(new ArrayList<>(List.of(
                new Card(14,Suit.BASTONI),
                new Card(1,Suit.BASTONI),
                new Card(10,Suit.COPPE),
                new Card(9,Suit.COPPE),
                new Card(13,Suit.DENARI),
                new Card(12,Suit.DENARI),
                new Card(11,Suit.DENARI),
                new Card(10,Suit.DENARI),
                new Card(17,Suit.TRIONFI),
                new Card(16,Suit.TRIONFI),
                new Card(15,Suit.TRIONFI),
                new Card(14,Suit.TRIONFI),
                new Card(13,Suit.TRIONFI),
                new Card(12,Suit.TRIONFI),
                new Card(2,Suit.TRIONFI),
                new Card(1,Suit.TRIONFI),
                new Card(0,Suit.TRIONFI))),0);
        assertArrayEquals(new ArrayList<>(List.of(
                new Card(10,Suit.COPPE),
                new Card(9,Suit.COPPE)
        )).toArray(), State.scartataIniziale(player).toArray());
    }
}