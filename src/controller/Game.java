package controller;

import model.Board;
import model.player.Player;
import model.server.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    protected Board board;
    public Player Player1;
    public Player Player2;
    public String gameName;
    public List<Player> players= new ArrayList<>();
    public Player currentPlayer;

    public Game(String gameName){
        this.gameName = gameName;
        board = new Board();
        board.generateBoard();

    }
    public Game() {
        board = new Board();
        board.generateBoard();
    }

    /**
     * The main logic of the game is inside this method.
     */
    public void play(){
        Random x =new Random();
        int turn = x.nextInt(2);
        int [] move;
        while(!endGame()){
            if(turn%2 == 0) currentPlayer = Player1;
            else currentPlayer = Player2;
            System.out.println(currentPlayer.getName()+ " makes a move!");
            System.out.println(board.returnBoard());
            move =currentPlayer.getMove(this);
            board.setPointers(move[1],move[2] );
            if(availableSingleMove()){
                if(checkSingleMove()){
                    currentPlayer.addMarbles(board.Move());
                    turn++;
                }else{
                    System.out.println("Single move not valid!");
                }
            }else {
                currentPlayer.addMarbles(board.Move());
                board.setPointers(move[3],move[4]);
                if(checkDoubleMove()){
                    currentPlayer.addMarbles((board.Move()));
                    turn++;
                }else{
                    System.out.println("Double move not valid");
                }
            }
        }

        System.out.println(board.returnBoard());
        System.out.println("The game has ended.");
        Reader.close();
        Player winner = determineWinner();
        if (winner == null) {
            System.out.println("Game ended in a draw!");
        } else {
            System.out.println(winner.name+" is the winner!");
        }
    }

    /** Checks weather the game has ended or not.
     * @return true if the game is still going.
     */
    public Boolean endGame() {
        return !availableSingleMove() && !availableDoubleMove();
    }
    /**
     * Checks if any legal double moves can be performed on the board.
     *
     * @return true if any legal double moves exists
     */
    private Boolean availableDoubleMove() {
        Game gameCopy = clone();

        for (int x = 1; x < board.fieldSize + 1; x ++) {
            for (int y = 1; y < board.fieldSize + 1; y ++) {

                gameCopy.getBoard().setPointerX(x);
                gameCopy.getBoard().setPointerY(y);

                if (gameCopy.getBoard().pointerSide() != -1 && gameCopy.getBoard().pointerSide() != 0
                        && gameCopy.checkDoubleMove()) return true;
            }
        }

        return false;
    }
    /**
     * Checks if any legal single moves can be performed on the board.
     *
     * @return true if any legal single moves exists
     */
    public Boolean availableSingleMove() {
        Game copy = clone();
        for (int x = 1; x <= board.fieldSize ; x ++) {
            for (int y = 1; y <= board.fieldSize ; y ++) {
                copy.getBoard().setPointerX(x);
                copy.getBoard().setPointerY(y);
                if (copy.getBoard().pointerSide() != -1 && copy.getBoard().pointerSide() != 0
                        && copy.checkSingleMove()) return true;
            }
        }
        return false;
    }

    /**
     * Checks if the move can be made.
     *
     * @return true if move can be performed
     */
    public Boolean checkSingleMove() {
        Game copy = clone();
        copy.getBoard().setFields(board.copy());
        copy.getBoard().setPointerX(board.pointerX);
        copy.getBoard().setPointerY(board.pointerY);
        copy.getBoard().pushMarbles();
        return copy.getBoard().removeAdjacent().size() != 0;
    }
    /**
     * Checks if the double move can be performed based on input.
     *
     * @return true if double move can be performed
     */
    public Boolean checkDoubleMove() {
        if (checkSingleMove()) return false;
        Game copy = clone();
        copy.getBoard().Move();
        return copy.checkSingleMove();
    }

    /**
     * Determines the winner.
     *
     * @return winner of the game
     * @ensures null if draw
     */
    public Player determineWinner() {
        int scoreA = Player1.countPoints();
        int scoreB = Player2.countPoints();

        if (scoreA > scoreB) {
            return Player1;
        } else if (scoreA < scoreB) {
            return Player2;
        } else {
            if (Player1.score.size() > Player2.score.size()) {
                return Player1;
            } else if (Player1.score.size() < Player2.score.size()) {
                return Player1;
            }
        }
        return null;
    }

    public Board getBoard(){
        return board;
    }
    /*
     * Clones the board.
     */
    @Override
    protected Game clone() {
        Game gameCopy = new Game();
        gameCopy.getBoard().setFields(board.copy());
        gameCopy.getBoard().setPointerX(board.pointerX);
        gameCopy.getBoard().setPointerY(board.pointerY);
        return gameCopy;
    }
    public void addPlayers(Player p1,Player p2){
        players.add(p1);
        players.add(p2);
    }
    public List<Player> getPlayers(){
        return this.players;
    }
    public void switchPlayer(){
        for(Player p :players){
            if(!p.equals(getCurrent())){
                setCurrent(p);
            }
        }
    }
    public Player getCurrent(){
        return currentPlayer;
    }
    public void setCurrent(Player p){
        currentPlayer = p;
    }

}
