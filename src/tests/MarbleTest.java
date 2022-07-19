package tests;

import model.Marble;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Daniel Botnarenco
 */
public class MarbleTest {

    @Test
    public void testIndexToMarble(){
        assertEquals(Marble.BLUE, Marble.indexToMarble(1));
        assertEquals(Marble.ORANGE, Marble.indexToMarble(2));
        assertEquals(Marble.RED, Marble.indexToMarble(3));
        assertEquals(Marble.YELLOW, Marble.indexToMarble(4));
        assertEquals(Marble.PURPLE, Marble.indexToMarble(5));
        assertEquals(Marble.GREEN, Marble.indexToMarble(6));
        assertEquals(Marble.EMPTY, Marble.indexToMarble(0));
        assertEquals(Marble.NULL, Marble.indexToMarble(-2));
    }

    @Test
    public void testGetIndex(){
        assertEquals(1,Marble.BLUE.getIndex());
        assertEquals(2,Marble.ORANGE.getIndex());
        assertEquals(3,Marble.RED.getIndex());
        assertEquals(4,Marble.YELLOW.getIndex());
        assertEquals(5,Marble.PURPLE.getIndex());
        assertEquals(6,Marble.GREEN.getIndex());
        assertEquals(0,Marble.EMPTY.getIndex());
        assertEquals(-2,Marble.NULL.getIndex());
    }



}
