package dev.macrobug.mcts4j.games.tarot;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

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
}
