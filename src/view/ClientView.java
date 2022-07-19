package view;

import controller.CollectoClient;
import exceptions.ServerUnavailableException;
import exceptions.TOOFEWARGUMENTS;
import protocol.ProtocolMessages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientView {

    private final Scanner s = new Scanner(System.in);

    private final CollectoClient client;

    public ClientView(CollectoClient client) {
        this.client = client;
    }

    /**
     * Starts a new client view. Take input from the user and sent it to handleUserInput().
     *
     * @throws ServerUnavailableException if IO errors occur.
     */
    public synchronized void start() throws ServerUnavailableException {
        printHelpMenu();
        s.nextLine();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showMessage("Enter command: ");
            try {
                handleUserInput(getLine());
            } catch (TOOFEWARGUMENTS e) {
                showMessage(e.getMessage());
            }
        }
    }

    /**
     * Calls a function of the AbaloneClient depending on the user input.
     *
     * @param input input read from the user
     * @throws ServerUnavailableException if IO errors occur
     * @throws TOOFEWARGUMENTS            if the input does not have enough arguments
     */
    private void handleUserInput(String input) throws ServerUnavailableException, TOOFEWARGUMENTS, ServerUnavailableException {

        String[] split = input.split("~");
        if ((split[0].equals(ProtocolMessages.JOINGAME) && split.length < 3)
                || (split[0].equals(ProtocolMessages.CREATEGAME) && split.length < 4)) {
            throw new TOOFEWARGUMENTS("The command has too few arguments. Please look at the help menu.");
        }

        switch (split[0]) {
            case ProtocolMessages.CHAT:
                client.chatMessage(split[1]);
                break;
            case ProtocolMessages.LOGIN:
                client.login(split[1]);
                break;
            case ProtocolMessages.LIST:
                client.getList();
                break;
            case ProtocolMessages.QUEUE:
                client.enterQueue();
                break;
            case ProtocolMessages.HELLO:
                String result="";
                for(int i=1;i<split.length;i++){
                    result += "~"+split[i];
                }
                client.doHello(result);
                break;
            case ProtocolMessages.MOVE:
                String caca="";
                if(split.length>2){
                    caca = split[1]+ProtocolMessages.DELIMITER+split[2];
                    client.move(caca);
                    break;
                }
                caca = split[1];
                client.move(caca);
                break;
            default:
                showMessage("Unknown command. Please follow the");
                printHelpMenu();
                break;
        }

    }

    /**
     * Reads a port from the scanner and checks if it's valid.
     *
     * @ensures /result >=1 && /result <=65535
     */
    public int getPort() {
        int i = -1;
        while (i < 0 || i > 65535) {
            try {
                showMessage("Connecting to port: ");
                i = s.nextInt();
            } catch (InputMismatchException e) {
                s.next();
            }
        }

        return i;
    }

    /**
     * Reads a string from the scanner.
     *
     * @param message question to be printed beforehand
     * @return the string that the user entered.
     */
    private String getString(String message) {
        showMessage(message);
        String string;
        while (!s.hasNext()) {
            s.next();
        }
        string = s.next();
        return string;
    }

    /**
     * Reads a string from the scanner and translates it into a boolean.
     *
     * @param question question to be printed beforehand
     * @return the string that the user entered.
     */
    public boolean getBoolean(String question) {
        showMessage(question + "(yes/no answer): ");
        String response = s.next();
        return response.equals("yes");
    }

    /**
     * Reads an entire line from the scanner and returns it.
     *
     * @ensures /result ends with "\n"
     */
    public String getLine() {
        return s.nextLine();
    }

    /**
     * Checks if the given string is a valid IP address.
     *
     * @param ip the string representation of the IP.
     * @return true if the string is valid, false otherwise.
     */
    private static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Reads a string from the scanner and translates it into an InetAddress.
     *
     * @ensures validIP() returns true && /result != null;
     */
    public InetAddress getIp() {
        InetAddress ipp = null;

        String ip = getString("Enter a valid IP Address: ");
        while (!validIP(ip)) {
            ip = getString("The entered IP address is not valid. Please type again: ");
        }
        try {
            ipp = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipp;
    }

    /**
     * Prints a message on the console.
     *
     * @param s message to be printed.
     */
    public void showMessage(String s) {
        System.out.println(s);
    }

    /**
     * Prints the help menu on the console.
     */
    private void printHelpMenu() {
        System.out.println();
        System.out.println("HELP MENU: \n" + " HELLO~<client description>[~extension]* \n"
                + " LOGIN~<username> \n" + " LIST \n"
                + " QUEUE \n " +  " SENDMESSAGE <message> \n" + " MOVE~<first push>[~second push] \n");
    }

}
