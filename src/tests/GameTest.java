package tests;

import controller.Game;
import controller.NetworkedGame;
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

public class GameTest {
    Game g;
    NetworkedGame ng;

    @BeforeEach
    void setUp(){
        //GameClientHandler c1 = new GameClientHandler();

        g = new Game();
        //ng = new NetworkedGame();
    }

    /**
     * Tests if players are added to the game.
     */
    @Test
    void testAddPlayers(){
        List<Player> playerList = new ArrayList<>();
        Player p1 = new HumanPlayer("Daniel");
        Player p2 = new HumanPlayer("George");
        playerList.add(p1);
        playerList.add(p2);
        g.addPlayers(p1,p2);
        assertEquals(g.getPlayers(),playerList);
    }

    /**
     * Tests if after the game is created if available moves can be made on the board.
     */
    @Test
    void testGameLogic(){
        assertEquals(true,g.availableSingleMove());
        assertEquals(false , g.endGame());
    }


    /**
     * Testing determine winner to see if the correct winner is being chosen.
     */
    @Test
    void testDetermineWinner(){
        g.Player1 = new HumanPlayer("Daniel");
        g.Player2 = new HumanPlayer("Random");
        g.getBoard().setBoard("6~2~0~0~0~0~0~0~0~0~0~0~0~0~6~2~0~0~0~0~3~0~0~0~0~0~0~0~6~2~0~0~0~0~3~0~0~0~0~0~0~0~0~0~0~0~0~0~3~");
        assertEquals(null,g.determineWinner());
        //MOVE~27
        g.getBoard().setPointers(2,7);
        g.Player2.addMarbles(g.getBoard().Move());
        //Test if the player 2 is winning the game after a move
        assertEquals(g.Player2.getName(),g.determineWinner().getName());

        //MOVE~21
        g.getBoard().setPointers(2,1);
        g.Player1.addMarbles(g.getBoard().Move());
        //MOVE~22
        g.getBoard().setPointers(2,2);
        g.Player1.addMarbles(g.getBoard().Move());

        //Test uf the winner is changhed after scoring more points than the first player
        assertEquals(g.Player1.getName(),g.determineWinner().getName());

    }

}
