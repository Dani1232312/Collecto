package controller;

import controller.client.GameListener;
import exceptions.ExitProgram;
import exceptions.ServerUnavailableException;
import model.Board;
import protocol.ProtocolMessages;
import view.ClientView;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client for Networked Collecto game.
 *
 * @author Daniel Botnarenco
 */
public class CollectoClient {
    public BufferedReader in;
    public ClientView view;
    private Socket serverSock;
    private BufferedWriter out;
    private Board board;

    /**
     * Constructs a new CollectoClient. Initialises the view.
     */
    public CollectoClient() {
        view = new ClientView(this);
        board = new Board();
    }

    /**
     * Starts a new CollectoClient by creating a connection. The view is then
     * started. The view asks for used input and makes calls to methods of this
     * class. When errors occur, or when the user terminates a server connection,
     * the user is asked whether a new connection should be made.
     */
    public void start() {
        try {
            createConnection();
            view.start();
        } catch (ServerUnavailableException | ExitProgram e) {
            closeConnection();
        }
    }

    /**
     * Creates a connection to the server. Requests the IP and port to connect to at
     * the view (TUI). The method continues to ask for an IP and port and attempts
     * to connect until a connection is established or until the user indicates to
     * exit the program.
     *
     * @throws ExitProgram if a connection is not established and the user indicates
     *                     to want to exit the program.
     * @ensures serverSock contains a valid socket connection to a server
     */
    public void createConnection() throws ExitProgram {
        clearConnection();
        while (serverSock == null) {
            InetAddress addr = view.getIp();
            int port = view.getPort();

            // try to open a Socket to the server
            try {
                view.showMessage("Attempting to connect to " + addr + ":" + port + "...");
                serverSock = new Socket(addr, port);
                in = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
                view.showMessage("Connection successful.");

                // Initialize the listener, that will take all the messages coming from the
                // server and forward them to the view
                GameListener listener = new GameListener(this);
                (new Thread(listener)).start();

            } catch (IOException e) {
                view.showMessage("no");
                //"ERROR: could not create a socket on " + addr.getHostAddress() + " and port " + port + ".");

                // Do you want to try again?
                if (!view.getBoolean("Do you want to try again?")) {
                    throw new ExitProgram("User indicated to exit.");
                } else {
                    start();
                }
            }
        }
    }

    /**
     * Resets the serverSocket and In- and OutputStreams to null. Always make sure
     * to close current connections before calling this method!
     *
     * @ensures in == null && out = null && serverSock == null;
     */
    void clearConnection() {
        serverSock = null;
        in = null;
        out = null;
    }
    // -------- Protocol methods -----------------------------------------------

    /**
     * Sends a message to the connected server, followed by a new line. The stream
     * is then flushed.
     *
     * @param msg the message to write to the OutputStream.
     * @throws ServerUnavailableException if IO errors occur.
     * @requires msg != null
     * @ensures the message is wrote and flushed to the OutputStream
     */

    private synchronized void sendMessage(String msg) throws ServerUnavailableException {
        if (out != null) {
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new ServerUnavailableException("Could not write " + "to server.");
            }
        } else {
            throw new ServerUnavailableException("Could not write " + "to server.");
        }
    }

    /**
     * @param arguments arguments to do hello on the server.
     * @throws ServerUnavailableException is thrown if the server cannot process the request.
     */
    public void doHello(String arguments)throws ServerUnavailableException{
        String[] split = arguments.split("~");
        String message;
        if(split.length>1){
            message = ProtocolMessages.HELLO+ProtocolMessages.DELIMITER+split[0]+split[1];
        }else{
            message = ProtocolMessages.HELLO+split[0];
        }
        sendMessage(message);


    }

    public void enterQueue() throws ServerUnavailableException{
        String message = ProtocolMessages.QUEUE;
        sendMessage(message);
    }

    public void login(String name)throws ServerUnavailableException{
        String message = ProtocolMessages.LOGIN+ProtocolMessages.DELIMITER+name;
        sendMessage(message);
    }
    public void getList()throws ServerUnavailableException{
        String message = ProtocolMessages.LIST;
        sendMessage(message);
    }

    public void move(String move)throws ServerUnavailableException{
        String[] split = move.split("~");
        String message;
        if(split.length == 2){
            message = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+split[0]+ProtocolMessages.DELIMITER+split[1];
        }else{
            message = ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+split[0];
        }
        sendMessage(message);
    }
    /**
     * Sends a request to send a message to the server.
     *
     * @param message message to be sent
     * @throws ServerUnavailableException if IO errors occur.
     */
    public void chatMessage(String message) throws ServerUnavailableException {
        // Build the message
        String msg = ProtocolMessages.CHAT + ProtocolMessages.DELIMITER + message;

        // Send it to the server
        sendMessage(msg);
    }
    // -- Additional methods -----------------------------------------------

    /**
     * Closes the connection by closing the In- and OutputStreams, as well as the
     * serverSocket.
     */
    public void closeConnection() {
        System.out.println("Closing the connection...");
        try {
            if (in != null) {
                in.close();
                out.close();
                serverSock.close();
                view.showMessage("");
                CollectoClient.main(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method starts a new AbaloneClient.
     */
    public static void main(String[] args) {
        (new CollectoClient()).start();
    }

    public Board getBoard(){
        return board;
    }
}
