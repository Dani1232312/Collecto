package view;

import model.Board;

import java.util.Scanner;

public class GameView {
    private static Board board;
    private final Scanner s = new Scanner(System.in);

    public GameView(Board board) {
        GameView.board = board;
    }

    /**
     * Prints a message to the console, ended by a new line.
     *
     * @param s message to be printed.
     */
    public void showMessage(String s) {
        System.out.println(s);
    }

    /**
     * Prints a message to the console.
     *
     * @param s message to be printed.
     */
    public void showMessage2(String s) {
        System.out.print(s);
    }

    /**
     * Reads an integer from the scanner and returns it.
     *
     * @ensures /result >=0
     */
    public int getInt() {
        int i;
        do {
            while (!s.hasNextInt()) {
                showMessage("Please enter a positive number!");
                s.next(); // this is important!
            }
            i = s.nextInt();
        } while (i <= 0);

        return i;
    }
    public int[] getIntArray() {
        System.out.print("Perform move: ");
        String command = s.nextLine();
        int[] output = new int[command.length()];

        for (int i = 0; i != output.length; i ++) {
            switch (command.charAt(i)) {
                case '1':
                    output[i] = 0;
                    break;
                case 'a':
                    output[i] = 3;
                    break;
                case 'A':
                    output[i] = 3;
                    break;
                case 'w':
                    output[i] = 1;
                    break;
                case 'W':
                    output[i] = 1;
                    break;
                case 's':
                    output[i] = 2;
                    break;
                case 'S':
                    output[i] = 2;
                    break;

                case 'd':
                    output[i] = 4;
                    break;
                case 'D':
                    output[i] = 4;
                    break;
            }
        }

        return output;
    }

    /**
     * Reads a string from the scanner.
     *
     * @return the string that the user entered.
     */
    public String getString() {
        String string;
        while (!s.hasNext()) {
            s.next();
        }
        string = s.next();
        return string;
    }

    /**
     * Reads an entire line from the scanner and returns it.
     *
     * @ensures /result ends with "\n"
     */
    public String getLine() {
        return s.nextLine();
    }

    public String printBoard(int x) {
        StringBuilder row = new StringBuilder();

        for(int j=0;j<7;j++) {
            row.append(board.getMarble(x, j).toString()).append(" ");
        }

        return row.toString();
    }
}
