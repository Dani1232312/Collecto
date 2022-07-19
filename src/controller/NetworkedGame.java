package controller;

import model.Marble;
import model.player.HumanPlayer;
import model.player.Player;
import model.server.Lobby;
import protocol.ProtocolMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import  model.Direction;
//import  model.Move;
//import  model.MoveType;

public class NetworkedGame extends Game implements Runnable{
    /**
     * The lobby where the players are coming from.
     */
    private final Lobby lobby;

    public boolean finished = false;



    /**
     * Creates a new networked game. Uses the name and the players of the lobby.
     *
     * @param lobby from which the lobby is created
     */
    public NetworkedGame(Lobby lobby) {
        // create a game with the name of the lobby
        super(lobby.getName());
        //nrPlayers = lobby.getNrPlayers();
        this.lobby = lobby;
//        players.add(Player1);
//        players.add(Player2);
        //board = new Board();
        //players = new Player[nrPlayers + 1];
    }

    /**
     * If the game is still going, return false. If the game has finished either by
     * winning or draw, return true;
     */
    public synchronized boolean isFinished() {
        return finished;
    }

    @Override
    public synchronized void run() {
        // Adds the lobby players to the game
        List<GameClientHandler> clients = lobby.getClients();
        Player1 = new HumanPlayer(clients.get(0).getName());
        Player2 = new HumanPlayer(clients.get(1).getName());
        addPlayers(Player1,Player2);
        StringBuilder result = new StringBuilder();
        Marble[][] fields = board.getFields();
        for(int i=0;i< board.size;i++){
            for(int j=0;j<board.size;j++){
                // TODO: 28-Jan-21
                result.append(ProtocolMessages.DELIMITER).append(fields[i][j].getIndex());
            }
        }
        // Chose a new player to start the game at random
        int current = new Random().nextInt(2) ;
        String p1;
        String p2;
        if(current ==1){
            p1 = lobby.getClients().get(0).getName();
            p2 = lobby.getClients().get(1).getName();
        }else{
            p1 = lobby.getClients().get(1).getName();
            p2 = lobby.getClients().get(0).getName();
        }
        lobby.sendToAllClients(ProtocolMessages.NEWGAME+result+ProtocolMessages.DELIMITER+p1+ProtocolMessages.DELIMITER+p2);


    }



    public synchronized String makeMove(GameClientHandler client, String move) {
        String out;
        if (availableSingleMove()) {
            if (checkSingleMove()) {
                getPlr(client).addMarbles(Move());
                out = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move;
                // update the players about the move
                switchPlayer();
                //turns++;
                //lobby.sendToAllClients(out);
                //lobby.sendToAllClients(board.returnBoard());
                //lobby.sendToAllClients(getTurn());

                if (endGame()) {
                    finished = true;
                    String message;
                    if(determineWinner() == null){
                        out = ProtocolMessages.GAMEOVER+ProtocolMessages.DRAW;
                    }else{
                        String winner = determineWinner().getName();
                        out = ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+"WINNER"+ProtocolMessages.DELIMITER+winner;
                    }

                    //System.out.println("> Sending: " + message);
                    //lobby.sendToAllClients(message);
                }
            }else{
                out = ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"Invalid move";
            }
        } else if (checkDoubleMove()) {
            getPlr(client).addMarbles(board.Move());
            out = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move;

            switchPlayer();


            if (endGame()) {
                finished = true;
                String message;
                if(determineWinner() == null){
                    out = ProtocolMessages.GAMEOVER+ProtocolMessages.DRAW;
                }else{
                    String winner = determineWinner().toString();
                    out = ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+winner;
                }
                //message = ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER;
//                    System.out.println("> Sending: " + message);
//                    lobby.sendToAllClients(message);
            }
        }else {
            out = ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"Invalid move";
        }
        return out;


    }
    protected ArrayList<Marble> Move() {
        board.pushMarbles();
//        System.out.println("Board in game :\n" + board.returnBoard());
        return board.removeAdjacent();
    }

    public Player getPlayer(GameClientHandler client){
        Player player=null;
        for (GameClientHandler c : lobby.getClients()) {
            for (Player p : lobby.getGame().getPlayers()) {
                System.out.println(p.getName());
                if (client.getName().equals(c.getName())) {
                    player = p;
                    return player;
                }
            }
        }

        return player;
    }



    public Player getPlr(GameClientHandler client){
        for(Player p:lobby.getGame().getPlayers()){
            if(p.getName().equals(client.getName())){
                return p;
            }
        }
        return null;
    }
}
