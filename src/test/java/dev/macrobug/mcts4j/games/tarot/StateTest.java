package dev.macrobug.mcts4j.games.tarot;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class StateTest {
    @Test
    public void getPointsTest() {
        // NESSUNA PRESA
        assertEquals(0, State.getPoints(new ArrayList<>(),new Game(0)));
    }
}
