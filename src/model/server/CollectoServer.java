
package model.server;

import controller.GameClientHandler;
import exceptions.ExitProgram;
import protocol.ProtocolMessages;
import view.ServerView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server TUI for Networked Hotel Application
 * Intended Functionality: interactively set up & monitor a new server.
 *
 * @author Daniel Botnarenco
 */
public class CollectoServer implements Runnable {
    public final String serverDescription = "Daniel's Collecto Server~CHAT";
    private List<GameClientHandler> queue = new ArrayList<>();
    /**
     * List of GameClientHandlers, one for each connected client.
     */
    private final List<GameClientHandler> clients;
    /**
     * List of all the lobbies on the server.
     */
    private final List<Lobby> lobbies = new ArrayList<>();
    /**
     * The view of this AbaloneServer.
     */
    private final ServerView view;
    /**
     * The ServerSocket of this Abalone Server.
     */
    private ServerSocket ssock;
    /**
     * Next client number, increasing for every new connection.
     */
    private int next_client_no;

    //private Board board;

    /**
     * Constructs a new AbaloneServer. Initialize the clients list, the view and the
     * next_client_no.
     */
    public CollectoServer() {
        clients = new ArrayList<>();
        view = new ServerView();
        next_client_no = 1;
    }

    // ------------------ Main --------------------------

    /**
     * Start a new Abalone Server.
     */
    public static void main(String[] args) {
        CollectoServer server = new CollectoServer();
        System.out.println("Welcome to the Collecto Server! Starting...");
        new Thread(server).start();
    }

    /**
     * If the client shuts down without typing LEAVEGAME beforehand, remove him
     * from the lobbies that he was in (if any).
     *
     * @param client client that disconnected
     */
    public void shutDown(GameClientHandler client) {
        Lobby toRemove = null;
        for (Lobby l : lobbies) {
            if (l.getClients().contains(client)) {
                for (GameClientHandler clientHandler : l.getClients()) {
                    //Send an error message to everyone except the player that disconnected
                    if (!clientHandler.equals(client)) {
                        try {
                            clientHandler.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER
                                    + "The game has stopped because the client " + client.getName()
                                    + " has disconnected." + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                l.getClients().remove(client);
                l.currentPlayers--;
                if (l.isEmpty()) {
                    toRemove = l;
                }
            }
        }
        if (toRemove != null) {
            lobbies.remove(toRemove);
        }
    }

    /**
     * Opens a new socket by calling {@link #setup()} and starts a new
     * GameClientHandler for every connecting client.
     * If {@link #setup()} throws a ExitProgram exception, stop the program. In case
     * of any other errors, ask the user whether the setup should be ran again to
     * open a new socket.
     */
    @Override
    public void run() {
        boolean openNewSocket = true;
        boolean ok = true;
        while (openNewSocket) {
            try {
                // Sets up the game server
                setup();

                while (ok) {
                    Socket sock = ssock.accept();
                    String name = "Client " + String.format("%02d", next_client_no++);
                    view.showMessage("New client [" + name + "] connected!");
                    GameClientHandler handler = new GameClientHandler(sock, this, name);
                    new Thread(handler).start();
                    clients.add(handler);
                }

            } catch (ExitProgram e1) {
                // If setup() throws an ExitProgram exception,
                // stop the program.
                openNewSocket = false;
            } catch (IOException e) {
                System.out.println("A server IO error occurred: " + e.getMessage());

                if (!view.getBoolean("Do you want to open a new socket?")) {
                    openNewSocket = false;
                }
            }
        }
        view.showMessage("See you later!");
    }
    // ------------------ Server Methods --------------------------

    /**
     * Opens a new ServerSocket at an user-defined Ip, on a user-defined port
     * The user is asked to input the ip, the port, after which a socket is
     * attempted to be opened. If the attempt succeeds, the method ends, If the
     * attempt fails, the user decides to try again, after which an ExitProgram
     * exception is thrown or a new port is entered.
     *
     * @throws ExitProgram if a connection can not be created on the given port and
     *                     the user decides to exit the program.
     * @ensures a serverSocket is opened.
     */
    private void setup() throws ExitProgram {
        ssock = null;
        while (ssock == null) {
            InetAddress ip = view.getIp("Please enter the server IP. If you don't know it, "
                    + "type ipconfig in cmd and look for the IPv4 address.");
            int port = view.getPort();
            // try to open a new ServerSocket
            try {
                view.showMessage("Attempting to open a socket at " + ip.getHostAddress() + " on port " + port + "...");
                ssock = new ServerSocket(port);
                view.showMessage("Server started at port " + port);
            } catch (IOException e) {
                view.showMessage(
                        "ERROR: could not create a socket on " + ip.getHostAddress() + " and port " + port + ".");

                if (!view.getBoolean("Do you want to try again?")) {
                    throw new ExitProgram("User indicated to exit the " + "program.");
                } else {
                    run();
                }
            }
        }
    }
    public ServerView getView() {
        return view;
    }

    public String getHello(GameClientHandler client){
        client.setHello(true);
        return ProtocolMessages.HELLO+ProtocolMessages.DELIMITER+serverDescription;
    }

    public String login(String name,GameClientHandler handler){
        if(handler.getHello()) {
            for (GameClientHandler client : clients) {
                if (client.getName().equals(name)) {

                    return ProtocolMessages.ALREADYLOGGEDIN;
                }
            }
            handler.setLogin(true);
            //clients.add(handler);
            handler.setName(name);
            return ProtocolMessages.LOGIN;
        }else {
            return ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"You need to make handshake with server first!";
        }
    }
    

    public synchronized void enterQueue(GameClientHandler client) throws IOException {
        if(client.getHello()) {
            if(client.getLogin()) {
                for (GameClientHandler clients : queue) {
                    if (clients.getName().equals(client.getName())) {
                        queue.remove(client);
                    }
                }
                queue.add(client);
                if (queue.size() > 1) {
                    createGame(queue.get(0), queue.get(1));
                }
            }else {
                client.write(ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"You need to log in first!");
            }
        }else{
            client.write(ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"You need to make handshake with server first!");
        }
    }


    /**
     * @param s is the string that is type (number)~(number) depending if is a single move or a double move
     * @param client is the client who is going to receive the points after the move.
     * @return a protocol
     */
    public synchronized String move(String s, GameClientHandler client) {
        String out = "";

        // search for the game in which the client is in
        boolean inGame = false;

        for (Lobby lobby : lobbies) {
//            System.out.println(lobby.getGame().getBoard().returnBoard());
            // A players only can make a move when he is in a lobby that is started
            if (lobby.getClients().contains(client) && lobby.isStarted()) {
                inGame = true;

                int[] move =new int[5];
                String[] split = s.split("~");
                if(split.length>2) {
                    move[0] = 2;
                    if(split[0].length()>1){
                        move[1]=Integer.parseInt(String.valueOf(split[0].charAt(0)));
                        move[2]=Integer.parseInt(String.valueOf(split[0].charAt(1)));
                    }else{
                        move[1]=0;
                        move[2]=Integer.parseInt(String.valueOf(split[1].charAt(0)));
                    }
                    convert(move, split);
                }
                else {
                    convert2(move, split);
                }
                lobby.getGame().getBoard().setPointers(move[1],move[2] );
                //Checks if any available single moves can be performed.
                if(lobby.getGame().availableSingleMove()) {
                    if(lobby.getGame().checkSingleMove()) {
                        lobby.getGame().getPlr(client).addMarbles(lobby.getGame().getBoard().Move());
                        //send back the move made
                        if(move[1] == 0){
                            out = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move[2];
                            System.out.println(out);
                        }else {
                            out = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move[1] + move[2];
                            System.out.println(out);

                        }
                        System.out.println(out);
                        lobby.sendToAllClients(out);
                        lobby.getGame().switchPlayer();
                        if(lobby.getGame().endGame()){
                            lobby.getGame().finished = true;
                            if(lobby.getGame().determineWinner() == null){
                                out = ProtocolMessages.GAMEOVER+ProtocolMessages.DRAW;
                                lobby.sendToAllClients(out);
                                break;
                            }else{
                                String winner = lobby.getGame().determineWinner().getName();
                                out = ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+"VICTORY"+ProtocolMessages.DELIMITER+winner;
                                //lobby.sendToAllClients(out);
                                lobby.sendToAllClients("out");
                                break;
                            }
                        }
                    }else{
                        out = ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"Invalid move";
                    }
                }else if(lobby.getGame().checkDoubleMove()) {

                    lobby.getGame().getPlr(client).addMarbles(lobby.getGame().getBoard().Move());

                    if(move[1]==0){
                        out = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move[2];
                    }else {
                        out = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move[1] + move[2];
                    }
                    if(move[3]==0){
                        out = out + ProtocolMessages.DELIMITER+move[4];
                    }else{
                        out = out + ProtocolMessages.DELIMITER+move[3]+move[4];
                    }

                    lobby.getGame().switchPlayer();
                    if (lobby.getGame().endGame()) {
                        lobby.getGame().finished = true;
                        if(lobby.getGame().determineWinner() == null){
                            out = ProtocolMessages.GAMEOVER+ProtocolMessages.DRAW;
                            lobby.sendToAllClients(out);
                        }else{
                            String winner = lobby.getGame().determineWinner().toString();
                            out = ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+winner;
                            lobby.sendToAllClients(out);
                        }
                    }
                }else {
                    out = ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+"Invalid move";
                }

                if (lobby.getGame().isFinished()) {
                    lobbies.remove(lobby);
                }
            }
        }
        if (!inGame) {
            return ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "You are not enrolled in any lobby/game.";
        }
        return out;
    }

    public static void convert2(int[] move, String[] split) {
        move[0] = 1;
        if(split[0].length()>1){
            move[1]=Integer.parseInt(String.valueOf(split[0].charAt(0)));
            move[2]=Integer.parseInt(String.valueOf(split[0].charAt(1)));
        }else{
            move[1]=0;
            move[2]=Integer.parseInt(String.valueOf(split[0].charAt(0)));
        }
    }

    public static void convert(int[] move, String[] split) {
        if(split[1].length()>1){
            move[3]=Integer.parseInt(String.valueOf(split[1].charAt(0)));
            move[4]=Integer.parseInt(String.valueOf(split[1].charAt(1)));
        }else{
            move[3]=0;
            move[4]=Integer.parseInt(String.valueOf(split[1].charAt(0)));
        }
    }

    /**
     * Sends a chat message to all the players in a lobby.
     *
     * @param client  the client that sends the message.
     * @param message the message to be sent.
     */
    public synchronized String chatMessage(GameClientHandler client, String message) {
        // search for the game in which the client is in
        boolean inGame = false;
        for (Lobby lobby : lobbies) {
            if (lobby.getClients().contains(client)) {
                inGame = true;
                String newMessage = ProtocolMessages.CHAT + ProtocolMessages.DELIMITER + client.getName()
                        + ProtocolMessages.DELIMITER + message;
                lobby.sendToAllClients(newMessage);
            }
        }

        if (!inGame) {
            return ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "You are not enrolled in any lobby/game.";
        }
        return "";

    }
    public synchronized String getError(String error){
        return ProtocolMessages.ERROR+ProtocolMessages.DELIMITER+error;
    }

    /**
     * @return a String of people who are on the server
     */
    public synchronized String listGames() {
        StringBuilder message = new StringBuilder();
        message.append(ProtocolMessages.LIST);
        for(GameClientHandler client:clients){
            message.append(ProtocolMessages.DELIMITER).append(client.getName());
        }
        return message.toString();
    }

    public synchronized void createGame(GameClientHandler player1, GameClientHandler player2){
        Lobby newLobby = new Lobby(player1.getName()+"'s lobby.");
        newLobby.enter(player1);
        newLobby.enter(player2);
        lobbies.add(newLobby);
        queue.remove(player1);
        queue.remove(player2);
    }
    
}