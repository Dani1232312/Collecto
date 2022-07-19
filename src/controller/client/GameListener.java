package controller.client;


import controller.CollectoClient;
import protocol.ProtocolMessages;

import java.io.IOException;

/**
 * Takes all input from the server, formats it and sends it to the client's
 * view. Each client has it's own GameListener. They all connected to the same
 * server.
 *
 * @author Daniel Botnarenco
 */
public class GameListener implements Runnable{
    private final CollectoClient client;
    /**
     * Creates a GameListener. Initializes the client.
     *
     * @param client represented by the GameListener
     */
    public GameListener(CollectoClient client) {
        this.client = client;
    }
    /**
     * Listen for input coming from the server in an infinite while loop. Format the
     * input and send it to the client's view.
     */
    @Override
    public synchronized void run() {
        try {
            while (true) {
                String line = client.in.readLine();
                if (line != null) {
                    client.view.showMessage(format(line));
                }
            }
        } catch (IOException e) {
            client.view.showMessage("The server has disconnected.");
            client.closeConnection();
        }
    }
    /**
     * Takes messages that come from the server and formats them so they look nice
     * for the client.
     *
     * @param line message coming from the server
     * @return formatted line
     */
    private String format(String line) {
        String[] splitted = line.split("~");
        int i;
        int j;
        switch (splitted[0]) {
            case ProtocolMessages.NEWGAME:
                StringBuilder result= new StringBuilder();
                for( i=1;i<50;i++){
                    result.append(splitted[i]).append(ProtocolMessages.DELIMITER);
                }
                client.getBoard().setBoard(result.toString());
                return "Player "+splitted[50]+" is going first."+"\n"+client.getBoard().returnBoard();
            case ProtocolMessages.MOVE:
                String nr = splitted[1];
                if(splitted.length>2){
                    String nr2 = splitted[2];
                    convert(nr);
                    convert(nr2);
//                    return "> Moving " + splitted[1] + "-" + splitted[2]+ "..."+"\n"+client.getBoard().returnBoard();
                }else{
                    convert(nr);
                }

                return "> Moving " + splitted[1] + "..."+"\n"+client.getBoard().returnBoard();
            case ProtocolMessages.LEAVEGAME:
                return splitted[1] + " has left the lobby/game.";
            case ProtocolMessages.GAMEOVER:
                if(splitted.length>2){
                    return ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+splitted[1]+ProtocolMessages.DELIMITER+splitted[2];
                }
                return ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+splitted[1];
            case ProtocolMessages.CHAT:
                return "> " + splitted[1] + ": " + splitted[2];
            case ProtocolMessages.ERROR:
                return "> " + splitted[1];

            default:

                break;
        }
        return line;

    }

    private void convert(String nr2) {
        int i;
        int j;
        if(nr2.length() == 2){
            i = Integer.parseInt(String.valueOf(nr2.charAt(0)));
            j = Integer.parseInt(String.valueOf(nr2.charAt(1)));
            client.getBoard().setPointers(i,j);
            client.getBoard().Move();
        }else{
            i = Integer.parseInt(String.valueOf(nr2.charAt(0)));
            client.getBoard().setPointer(i);
            client.getBoard().Move();
        }
    }

}
