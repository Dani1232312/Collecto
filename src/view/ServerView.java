package view;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Abalone Server TUI for user input and user messages
 */
public class ServerView {

    /**
     * The PrintWriter to write messages to
     */
    private final PrintWriter console;
    private final Scanner s = new Scanner(System.in);

    /**
     * Constructs a new HotelServerTUI. Initializes the console.
     */
    public ServerView() {
        console = new PrintWriter(System.out, true);
    }

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

    public void showMessage(String message) {
        console.println(message);
    }

    private String getString(String question) {
        showMessage(question);
        String string;
        while (!s.hasNext()) {
            s.next();
        }
        string = s.next();
        return string;
    }

    public int getPort() {
        int i = -1;
        while (i < 0 || i > 65535) {
            try {
                showMessage("Please enter the server port: ");
                i = s.nextInt();
            } catch (InputMismatchException e) {
                s.next();
            }
        }

        return i;
    }


    public boolean getBoolean(String question) {
        showMessage(question + "(yes/no answer): ");
        String response = s.next();
        return response.equals("yes");
    }

    public InetAddress getIp(String message) {
        showMessage(message);
        InetAddress ipp = null;

        String ip = getString("Enter a valid IP Addres: ");
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

}
