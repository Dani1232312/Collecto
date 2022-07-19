package model.server;

import controller.GameClientHandler;
import controller.NetworkedGame;
import protocol.ProtocolMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Lobby for the Collecto Networked Game.
 *
 * @author Daniel Botnarenco
 */

public class Lobby {
    private final String name;
    private final List<GameClientHandler> clients;
    private final int maxPlayers;
    public int currentPlayers;
    private boolean started;
    private NetworkedGame game;

    /**
     * Creates a new lobby. Initializes the ArrayList of clients. Sets started to
     * false and currentPlayers to 0.
     *
     * @param name       indicating the name of the lobby
     * @requires name != null && maxPlayers >=2 && maxPlayer <=4
     * @ensures getName() == name && getNrPlayers() == maxPlayers && getCurrentPlayers == 0
     * && isStarted == false && clients.size == 0
     */
    public Lobby(String name) {
        this.name = name;
        this.maxPlayers =2;
        clients = new ArrayList<>();
        started = false;
        currentPlayers = 0;
    }

    /**
     * Returns the index of 'client' in the 'clients' ArrayList.
     *
     * @param client client to be searched for
     * @return index of the client
     * @invariant the client with index 0 is the client that created the lobby
     * @invariant the client with index 1 is the first client that joined the lobby and so on
     * @requires client != null
     */
    int getClientIndex(GameClientHandler client) {
        for (int i = 0; i < maxPlayers; i++) {
            if (clients.get(i).equals(client)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds a player to the lobby.
     *
     * @param client the player to be added
     * @requires client != null
     * @ensures /old.getCurrentPlayers() + 1 == getCurrentPlayers()
     * @ensures clients.contains(client)
     * @ensures all the players in the lobby get an update
     */
    public synchronized void enter(GameClientHandler client) {
        clients.add(client);
        currentPlayers++;
        if (currentPlayers == maxPlayers) {
            started = true;
            new Thread(game = new NetworkedGame(this)).start();
        }

    }

    /**
     * Response when a client wants to leave a game. He is removed from the lobby and
     * the game is stopped if it was started.
     *
     * @param client that has left the game
     */
    public synchronized void leave(GameClientHandler client) {
        System.out.println(">Sending: " + ProtocolMessages.LEAVEGAME + ProtocolMessages.DELIMITER + client.getName());
        sendToAllClients(ProtocolMessages.LEAVEGAME + ProtocolMessages.DELIMITER + client.getName());
        if (!started) {
            clients.remove(client);
            currentPlayers--;
        } else {
            sendToAllClients(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER
                    + "The game has stopped because the client " + client.getName() + " has disconected.");
        }

    }

    /**
     * Returns true if the lobby does not have any player.
     */
    public boolean isEmpty() {
        return currentPlayers == 0;
    }

    /**
     * Returns the name of the lobby/game.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how many players can connect to this lobby.
     */
    public int getNrPlayers() {
        return maxPlayers;
    }

    /**
     * Returns how many players are currently in this lobby.
     */
    int getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Returns the game that is being played on the lobby.
     */
    public NetworkedGame getGame() {
        return game;
    }

    /**
     * True if the game has started. False otherwise.
     *
     * @invariant the game starts when all the players have connected.
     */
    boolean isStarted() {
        return started;
    }

    /**
     * Returns a list of clients that are in the lobby, represented as GameClientHandlers.
     */
    public List<GameClientHandler> getClients() {
        return clients;
    }

    /**
     * Sends a message to all the clients in the lobby.
     *
     * @param message the message to be sent
     * @requires message != null
     */
    public void sendToAllClients(String message) {
        for (GameClientHandler gameclient : clients) {
            try {
                gameclient.write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Lobby getLobby(){
        return this;
    }

}
