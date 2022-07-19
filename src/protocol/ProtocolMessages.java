package protocol;

/**
 * Protocol for Networked Hotel Application.
 *
 * @author DV Copae
 */

public class ProtocolMessages {

    /**
     * Delimiter used to separate arguments sent over the network.
     */
    public static final String DELIMITER = "~";

    /**
     * Sent as last line in a multi-line response to indicate the end of the text.
     */

    /**
     * The following chars are used by the client.
     */
    public static final String CREATEGAME = "CREATEGAME";
    public static final String FINDGAMES = "FINDGAMES";
    public static final String JOINGAME = "JOINGAME";
    public static final String MOVE = "MOVE";
    public static final String LEAVEGAME = "LEAVEGAME";
    public static final String SENDMESSAGE = "SENDMESSAGE";
    public static final String REQUESTLEADERBOARD = "REQUESTLEADERBOARD";
    public static final String HELLO = "HELLO";
    public static final String LOGIN = "LOGIN";
    public static final String LIST = "LIST";
    public static final String NEWGAME = "NEWGAME";
    public static final String GAMEOVER = "GAMEOVER";
    public static final String QUEUE = "QUEUE";
    public static final String ALREADYLOGGEDIN = "ALREADYLOGGEDIN";

    /**
     * The following chars are used by the server.
     */
    public static final String STARTGAME = "STARTGAME";
    public static final String VICTORY="VICTORY";
    public static final String DISCONNECT="DISCONNECT";
    public static final String DRAW = "DRAW";
    public static final String TURN = "TURN";
    public static final String INVALIDMOVE = "INVALIDMOVE";
    // MOVE is used again
    public static final String GAME = "GAME";
    public static final String PLAYER = "PLAYER";
    // LEAVEGAME IS USED AGAIN
    public static final String ENDGAME = "ENDGAME";
    public static final String ERROR = "ERROR";
    public static final String CHAT = "CHAT";
    public static final String SENDLEADERBOARD = "SENDLEADERBOARD";
}
