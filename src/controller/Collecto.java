package controller;

import model.player.HumanPlayer;
import java.util.Scanner;


/**
 * This is the main method than runs the Collecto game.
 */
public class Collecto {
    private static Game game;

    public static void main(String[] args) {
        game = new Game();
        setup();
        game.play();
    }


    public static void setup() {
        System.out.println("Welcome to Collecto game!");
        System.out.println("Made by Daniel Botnarenco");
        Scanner s = new Scanner(System.in);
        String name;
        System.out.print("Player 1 name:");
        name = s.nextLine();
        game.Player1 = new HumanPlayer(name);
        System.out.print("Player 2 name:");
        name = s.nextLine();
        game.Player2 = new HumanPlayer(name);
    }

}
