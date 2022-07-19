package model.player;

import controller.Game;
import model.Marble;

import java.util.ArrayList;

public abstract class Player {
public String name;
public ArrayList<Marble> score;
    public Player (String name){
        this.name = name;
        this.score = new ArrayList<>();
    }
    public void addMarbles(ArrayList<Marble> marbles) {
        score.addAll(marbles);
    }

    public int countPoints(){
        int[] count = new int[Marble.COLOURS];
        int finalScore = 0;
        for (int i = 0; i != score.size(); i ++) {
            count[score.get(i).getIndex() - 1] ++;
        }
        for (int i = 0; i != count.length; i ++) {
            finalScore += count[i]/3;
        }
        return finalScore;
    }
    public abstract int[] getMove( Game game);
    public String getName(){return this.name;}
}
