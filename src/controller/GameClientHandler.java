package controller;

import model.server.CollectoServer;
import protocol.ProtocolMessages;

import java.io.*;
import java.net.Socket;


/**
 * GameClientHandler for the Collecto Server application. This class can handle
 * the communication with one client.
 *
 * @author Daniel Botnarenco
 */
public class GameClientHandler implements Runnable {

    protected BufferedWriter out;

    /**
     * The socket and In- and OutputStreams.
     */
    private BufferedReader in;
    private Socket sock;
    /**
     * The connected CollectoServer.
     */
    private CollectoServer srv;
    /**
     * Name of this ClientHandler.
     */
    private String name;

    public boolean hello;

    /**
     * Checker if the client is logged in on the server
     */
    public boolean login;

    /**
     * Constructs a new GameClientHandler. Opens the In- and OutputStreams.
     *
     * @param sock The client socket
     * @param srv  The connected server
     * @param name The name of this ClientHandler
     */
    public GameClientHandler(Socket sock, CollectoServer srv, String name) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.srv = srv;
            this.name = name;
        } catch (IOException e) {
            shutdown();
        }
    }

    /**
     * Continuously listens to client input and forwards the input to the
     * {@link #handleCommand(String)} method.
     */
    @Override
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                srv.getView().showMessage("> [" + name + "] Incoming: " + msg);
                handleCommand(msg);
                msg = in.readLine();
            }
            shutdown();
        } catch (IOException e) {
            shutdown();
        }
    }

    /**
     * Returns the name of this client.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this client.
     *
     * @param name to be set
     * @ensures getName == name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sends a message to the Output Stream. The message is then flushed.
     *
     * @param string the message to be sent.
     * @throws IOException if IO errors occur
     */
    public void write(String string) throws IOException {
        out.write(string);
        out.newLine();
        out.flush();
    }

    /**
     * Handles commands received from the client by calling the according methods at
     * the CollectoServer.
     *
     * @param msg command from client
     * @throws IOException if an IO errors occur.
     * @invariant inputs received here are already valid because they were checked by the client before
     */
    private void handleCommand(String msg) throws IOException {
        String[] split = msg.split(ProtocolMessages.DELIMITER);
        String s;

        switch (split[0]) {
            case ProtocolMessages.QUEUE:
                srv.enterQueue(this);
                break;
            case ProtocolMessages.LIST:
                s = srv.listGames();
                out.write(s);
                out.newLine();
                out.flush();
                break;
            case ProtocolMessages.HELLO:
                s = srv.getHello(this);
                out.write(s);
                out.newLine();
                out.flush();
                break;
            case  ProtocolMessages.MOVE:
                if(split.length>2){
                    String result = split[1]+ProtocolMessages.DELIMITER+split[2];
                    s = srv.move(result,this);
                    if(!s.equals(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+result)){
                        out.write(s);
                        out.newLine();
                        out.flush();
                    }
                    break;
                    // TODO: 21-Jan-21
                }else{
                    s = srv.move(split[1],this);
                    //System.out.println("GAMECLIENTHANDLER: "+split[1]);
                    if(!s.contains(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+split[1])){
                        out.write(s);
                        out.newLine();
                        out.flush();
                    }
                    break;
                }
            case ProtocolMessages.LOGIN:
                s=srv.login(split[1],this);
                out.write(s);
                srv.getView().showMessage("> Sending: " + s);
                out.newLine();
                out.flush();
                break;
            case ProtocolMessages.CHAT:
                s=srv.chatMessage(this,split[1]);
                out.write(s);
                srv.getView().showMessage("> Sending: " + s);
                out.newLine();
                out.flush();
                break;
            case ProtocolMessages.ERROR:
                s = srv.getError(split[1]);
                out.write(s);
                srv.getView().showMessage("> Sending: " + s);
                out.newLine();
                out.flush();
                break;
            default:
                break;
        }

    }

    /**
     * Shut down the connection to this client by closing the socket and the In- and
     * OutputStreams. If other players were in the game when this client
     * disconnects, they return to their lobbies.
     */
    private void shutdown() {
        System.out.println("> [" + name + "] Shutting down.");
        // If the client shuts down without typing LEAVEGAME beforehand, remove him
        // from the lobbies that he was in (if any).
        srv.shutDown(this);
        try {
            in.close();
            out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getHello(){
        return hello;
    }

    public void setHello(boolean b){
        hello=b;
    }

    public void setLogin(boolean b){
        login=b;
    }

    public boolean getLogin(){
        return login;
    }

}
