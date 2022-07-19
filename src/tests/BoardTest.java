package tests;

import model.Board;
import model.Marble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Daniel Botnarenco
 */

class BoardTest {

    private Board b;
    private ArrayList<Marble> marbles;

    @BeforeEach
    void setUp() {
        b = new Board();
        marbles = new ArrayList<>();
    }

    /**
     * Tests if the generation of the board is correct.
     * If adjacent marbles are find then the test will result in false.
     */
    @Test
    void testBoardGeneration() {
        b.generateBoard();
        assertEquals(marbles, b.removeAdjacent());
    }

    /**
     * Tests if the move is properly done on a set move.
     */
    @Test
    void testMove(){
        b.setBoard("6~4~3~4~6~3~1~2~3~1~3~2~4~2~4~6~5~6~3~5~3~1~3~1~0~4~2~5~4~2~3~4~5~4~6~2~1~5~1~6~2~5~5~6~1~5~2~6~1~");
        //"MOVE~17"
        Marble[][] copy = b.copy();
        Board boardCopy = new Board();
        boardCopy.setFields(copy);
        boardCopy.setPointers(1,7);
        boardCopy.Move();
        b.setPointers(1,7);
        b.Move();
        for(int i = 0 ; i < boardCopy.size;i++){
            for(int j = 0 ; j < boardCopy.size;j++){
                assertEquals(boardCopy.getMarble(i,j),b.getMarble(i,j));
            }
        }
    }

    /**
     * Test if double move is performed corectlly.
     * testMove and testDoubleMove make use of all the other methods inside the board class such as removeMarbles. pushMarbles , updateRow and updateCollum
     */
    @Test
    public void testDoubleMove(){
        b.setBoard("6~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~6~");
        ArrayList<Marble> marbles = new ArrayList<>();
        //MOVE~21
        b.setPointers(2,1);
        b.Move();
        //MOVE~13
        b.setPointers(1,3);
        b.Move();
        assertEquals(marbles,b.removeAdjacent());
    }


}
