package tests;

import model.Board;
import model.Marble;
import model.player.HumanPlayer;
import model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Daniel Botnarenco
 */

public class PlayerTest {
    public Board b;
    public Player p;
    public List<Marble> marbles;
    public List<Marble> marbles2;

    @BeforeEach
    void setUp(){
        b = new Board();
        p = new HumanPlayer("Daniel");
        marbles = new ArrayList<>();
        marbles2 = new ArrayList<>();
    }

    /**
     * Testing the adding of points after each move.
     */
    @Test
    void testPointAdding() {
        //Add 3 marbles in our marble array to see if it will match the points after move.
        marbles.add(Marble.GREEN);
        marbles.add(Marble.GREEN);
        marbles.add(Marble.GREEN);
        b.setBoard("6~0~0~0~0~0~0~6~0~0~0~0~0~0~6~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~3~0~0~0~0~0~0~0~0~0~0~0~0~0~3~");
        //MOVE~21
        b.setPointers(2,1);
        p.addMarbles(b.Move());
        assertEquals(marbles,p.score );
        assertEquals(1,p.countPoints());

        marbles.add(Marble.RED);
        marbles.add(Marble.RED);

        //MOVE~27
        b.setPointers(2,7);
        p.addMarbles(b.Move());
        assertEquals(marbles,p.score);
        assertEquals(1,p.countPoints());
    }


}
