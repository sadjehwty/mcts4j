package dev.macrobug.mcts4j.games.tarot;

import org.junit.Test;

import java.util.ArrayList;
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
        ArrayList<Card> interoMazzo = new ArrayList<>(State.allCards);
        Game game=new Game(0,new Card[]{new Card(4,Suit.TRIONFI,0),new Card(4,Suit.TRIONFI,1),new Card(4,Suit.TRIONFI,2),new Card(4,Suit.TRIONFI,3)});
        assertEquals(939, State.getPoints(interoMazzo,game));
        interoMazzo.remove(Card.MATTO);
        assertEquals(939, State.getPoints(interoMazzo,game));
        // mano normale
        game=new Game(0,new Card[]{new Card(1,Suit.DENARI),new Card(4,Suit.TRIONFI,0),new Card(4,Suit.TRIONFI,1),new Card(4,Suit.TRIONFI,2)});
        interoMazzo = new ArrayList<>(State.allCards);
        ArrayList<Card> mano = new ArrayList<>(List.of(
                new Card(18,Suit.TRIONFI),
                new Card(15,Suit.TRIONFI),
                new Card(7,Suit.TRIONFI),
                new Card(4,Suit.TRIONFI,0),
                new Card(0,Suit.TRIONFI),
                new Card(1,Suit.SPADE),
                new Card(14,Suit.SPADE),
                new Card(12,Suit.SPADE),
                new Card(10,Suit.SPADE),
                new Card(9,Suit.SPADE),
                new Card(6,Suit.SPADE),
                new Card(1,Suit.BASTONI),
                new Card(14,Suit.BASTONI),
                new Card(10,Suit.BASTONI),
                new Card(9,Suit.BASTONI),
                new Card(6,Suit.BASTONI),
                new Card(11,Suit.COPPE),
                new Card(10,Suit.COPPE),
                new Card(8,Suit.COPPE),
                new Card(10,Suit.DENARI),
                new Card(9,Suit.DENARI)
                ));
        assertEquals(50,State.getPoints(mano,game));
        ArrayList<Card> mano2 = new ArrayList<>(interoMazzo);
        mano2.removeAll(mano);
        assertEquals(460,State.getPoints(mano2,game));
        game=new Game(0,new Card[]{
                new Card(1,Suit.DENARI),
                new Card(6,Suit.DENARI),
                new Card(7,Suit.DENARI),
                new Card(8,Suit.DENARI)
        });
        mano = new ArrayList<>(List.of(
                new Card(4,Suit.TRIONFI,0),
                new Card(4,Suit.TRIONFI,1),
                new Card(4,Suit.TRIONFI,2),
                new Card(4,Suit.TRIONFI,3),
                Card.BEGATTO,
                Card.MATTO
        ));
        assertEquals(36,State.getPoints(mano,game));
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
        )).toArray(), player.scartataIniziale().toArray());

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
        )).toArray(), player.scartataIniziale().toArray());
    }
}