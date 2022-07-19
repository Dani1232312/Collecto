package model;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    public final int size = 7;
    public final int center = 7/2;
    private Marble[][] fields;
    private int[] marbles;
//    public int pointerIndex;
    public int fieldSize;
    /*
    Pointer positions;
     */
    public int pointerX;
    public int pointerY;

    public Board(){
        fields = new Marble[size][size];
        marbles = new int[Marble.COLOURS+1];
//        pointerIndex = size*4;
        fieldSize =size+2;
        pointerX=1;
        pointerY=1;
    }
    /*
    Generation of the board
     */

    public void generateBoard(){
        int random;
        boolean reset = false;
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(j==center && i==center){
                    fields[i][j]=Marble.EMPTY;
                }else{
                    ArrayList<Marble> options = getOptions(i,j);
                    Random r = new Random();
                    try{
                        random = r.nextInt(options.size());
                        Marble marble = options.get(random);
                        marbles[marble.getIndex()-1] = marbles[marble.getIndex()-1] +1;
                        setMarble(i,j,marble);

                    }catch (IllegalArgumentException e){
                        reset = true;
                    }

                }
            }
        }
        if(reset) restart();
    }

    private void restart(){
        fields = new Marble[size][size];
        marbles= new int[Marble.COLOURS+1];
        generateBoard();
    }

    private ArrayList<Marble> getOptions(int x, int y) {
        ArrayList<Marble> output = new ArrayList<>();
        ArrayList<Marble> adjacent = getAdjacent(x, y);
        for (int i = 0; i != Marble.COLOURS; i ++) {
            if (marbles[i] < Marble.NR_MARBLES && !adjacent.contains(Marble.indexToMarble(i + 1))) {
                output.add(Marble.indexToMarble(i + 1));
            }
        }
        return output;
    }

    /** This is how a move is made
     * @return an array list of marbles
     */
    public ArrayList<Marble> Move() {
        pushMarbles();
        return removeAdjacent();
    }

    public void pushMarbles() {
        int location = pointerSide();
        if (location == 1 || location == 3) {
            updateColumn(compressBalls());
        } else if (location == 2 || location == 4) {
            updateRow(compressBalls());
        }
    }
    /**
     * Replaces row on current coordinates.
     * @param row of new Balls for replacement
     */
    private void updateRow(Marble[] row) {
        Marble[][] fields = getFields();
        for (int i = 0; i != size; i ++) {
            fields[pointerX - 2][i] = row[i];
        }
        setFields(fields);
    }
    /**
     * Replaces column on current coordinates.
     * @param column of new Balls for replacement
     */
    private void updateColumn(Marble[] column) {
        Marble[][] fields = getFields();
        for (int i = 0; i != size; i ++) {
            fields[i][pointerY - 2] = column[i];
        }
        setFields(fields);
    }

    public String returnBoard() {
        StringBuilder line = new StringBuilder();
        //String[][] ballNumColor = new String[size][size];
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < size; j ++) {
                line.append(fields[i][j].getNumColor()).append(Markers.DISTANCE);
            }
            line.append("\n");
        }
        return line.toString();
    }
/*
Setting of the board based on a string
 */
    public void setBoard(String board){
        String[] split = board.split("~");
        int nr=0;
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                switch (Integer.parseInt(split[nr])){
                    case 0:
                    case -1:
                        setMarble(i,j,Marble.indexToMarble(0));
                        nr++;
                        break;
                    case 1:
                        setMarble(i,j,Marble.indexToMarble(1));
                        nr++;
                        break;
                    case 2:
                        setMarble(i,j,Marble.indexToMarble(2));
                        nr++;
                        break;
                    case 3:
                        setMarble(i,j,Marble.indexToMarble(3));
                        nr++;
                        break;
                    case 4:
                        setMarble(i,j,Marble.indexToMarble(4));
                        nr++;
                        break;
                    case 5:
                        setMarble(i,j,Marble.indexToMarble(5));
                        nr++;
                        break;
                    case 6:
                        setMarble(i,j,Marble.indexToMarble(6));
                        nr++;
                        break;
                }
            }
        }
    }

    public ArrayList<Marble> getAdjacent(int x, int y) {
        ArrayList<Marble> adjacent = new ArrayList<>();
        if (x == 0) {
            adjacent.add(fields[x + 1][y]);
        } else if (x == size - 1) {
            adjacent.add(fields[x - 1][y]);
        } else {
            adjacent.add(fields[x + 1][y]);
            adjacent.add(fields[x - 1][y]);
        }
        if (y == 0) {
            adjacent.add(fields[x][y + 1]);
        } else if (y == size - 1) {
            adjacent.add(fields[x][y - 1]);
        } else {
            adjacent.add(fields[x][y + 1]);
            adjacent.add(fields[x][y - 1]);
        }
        return adjacent;
    }

    /** Removes adjacent marbles
     * @return an array list of marbles
     */
    public ArrayList<Marble> removeAdjacent() {
        ArrayList<Marble> output = new ArrayList<>();
        Marble[][] copy = copy();
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < size; j ++) {
                if (getAdjacent(i, j).contains(fields[i][j]) && fields[i][j] != Marble.EMPTY) {
                    output.add(fields[i][j]);
                    copy[i][j] = Marble.EMPTY;
                }
            }
        }
        fields = copy;
        return output;
    }

    /** Setting of pointers based on input
     * @param i 1st digit
     * @param j 2nd digit
     */
    public void setPointers(int i, int j){
        if(i == 1){
            setPointerY(1);
            switch (j){
                case 0:
                    setPointerX(5);
                    break;
                case 1:
                    setPointerX(6);
                    break;
                case 2:
                    setPointerX(7);
                    break;
                case 3:
                    setPointerX(8);
                    break;
                case 4:
                    setPointerY(2);
                    setPointerX(9);
                    break;
                case 5:
                    setPointerY(3);
                    setPointerX(9);
                    break;
                case 6:
                    setPointerY(4);
                    setPointerX(9);
                    break;
                case 7:
                    setPointerY(5);
                    setPointerX(9);
                    break;
                case 8:
                    setPointerY(6);
                    setPointerX(9);
                    break;
                case 9:
                    setPointerY(7);
                    setPointerX(9);
                    break;

            }
        }else if(i==2){
            switch (j){
                case 0:
                    setPointerX(9);
                    setPointerY(8);
                    break;
                case 1:
                    setPointerX(1);
                    setPointerY(2);
                    break;
                case 2:
                    setPointerX(1);
                    setPointerY(3);
                    break;
                case 3:
                    setPointerX(1);
                    setPointerY(4);
                    break;
                case 4:
                    setPointerX(1);
                    setPointerY(5);
                    break;
                case 5:
                    setPointerX(1);
                    setPointerY(6);
                    break;
                case 6:
                    setPointerX(1);
                    setPointerY(7);
                    break;
                case 7:
                    setPointerX(1);
                    setPointerY(8);
                    break;

            }
        }else
            {
            setPointerX(1);
            switch (j){
                case 0:
                    setPointerX(2);
                    setPointerY(9);
                    break;
                case 1:
                    setPointerY(9);
                    setPointerX(3);
                    break;
                case 2:
                    setPointerY(9);
                    setPointerX(4);
                    break;
                case 3:
                    setPointerX(5);
                    setPointerY(9);
                    break;
                case 4:
                    setPointerY(9);
                    setPointerX(6);
                    break;
                case 5:
                    setPointerY(9);
                    setPointerX(7);
                    break;
                case 6:
                    setPointerY(9);
                    setPointerX(8);
                    break;
                case 7:
                    setPointerY(1);
                    setPointerX(2);
                    break;
                case 8:
                    setPointerX(3);
                    setPointerY(1);
                    break;
                case 9:
                    setPointerX(4);
                    setPointerY(1);
                    break;

            }
        }
    }

    /** Setting of the pointer based on input
     * @param i a digit
     */
    public void setPointer(int i) {
        if (i >= 0 && i <= 6) {
            setPointerY(9);
            switch (i) {
                case 0:
                    setPointerX(2);
                    break;
                case 1:
                    setPointerX(3);
                    break;
                case 2:
                    setPointerX(4);
                    break;
                case 3:
                    setPointerX(5);
                    break;
                case 4:
                    setPointerX(6);
                    break;
                case 5:
                    setPointerX(7);
                    break;
                case 6:
                    setPointerX(8);
                    break;
            }
        } else if (i >= 7 && i <= 9) {
            setPointerY(1);
            switch (i) {
                case 7:
                    setPointerX(2);
                    break;
                case 8:
                    setPointerX(3);
                    break;
                case 9:
                    setPointerX(4);
                    break;
            }
        }
    }
    /**
     * Returns the side of the pointer.
     *
     * @return code of the pointer location
     */
    public int pointerSide() {
        //pointerIndex
        if ((pointerX == 1 && pointerY == 1) || (pointerX == 1 && pointerY == fieldSize) || (pointerX == fieldSize && pointerY == 1) || (pointerX == fieldSize && pointerY == fieldSize)) {
            return 0;
        } else if (pointerX == 1) {return 1;
        } else if (pointerY == fieldSize) {return 2;
        } else if (pointerX == fieldSize && pointerY < fieldSize) {return 3;
        } else if (pointerY == 1) {return 4;
        }
        return -1;
    }
    /**
     * Pushes Marbles
     * @return A Marble array.
     * @ensures !newBallSet.contains(Ball.EMPTY)
     */
    private Marble[] compressBalls() {
        ArrayList<Marble> marbles = getMarbles();
        Marble[] newBallSet = new Marble[size];
        int side = pointerSide();

        if (side == 1 || side == 4) {
            int j = 0;
            for (int i = size - marbles.size(); i != size; i ++) {
                newBallSet[i] = marbles.get(j);
                j ++;
            }
        } else {
            for (int i = 0; i != marbles.size(); i ++) {
                newBallSet[i] = marbles.get(i);
            }
        }

        for (int i = 0; i != newBallSet.length; i ++) {
            if (newBallSet[i] == null) newBallSet[i] = Marble.EMPTY;
        }

        return newBallSet;
    }
    private ArrayList<Marble> getMarbles() {
        ArrayList<Marble> marbles = new ArrayList<>();
        Marble[][] fields = getFields();
        int side = pointerSide();

        //horizontal
        if (side == 2 || side == 4) {
            for (int i = 0; i != size; i += 1) {
                if (!fields[pointerX - 2][i].equals(Marble.EMPTY)) {
                    marbles.add(fields[pointerX - 2][i]);
                }
            }

            //vertical
        } else {
            for (int i = 0; i != size; i += 1) {
                if (!fields[i][pointerY - 2].equals(Marble.EMPTY)) {
                    marbles.add(fields[i][pointerY - 2]);
                }
            }
        }

        return marbles;
    }
    public void setPointerX(int pointerX) {
        this.pointerX = pointerX;
    }

    public void setPointerY(int pointerY) {
        this.pointerY = pointerY;
    }


    public Marble getMarble(int x, int y) {
        return fields[x][y];
    }

    public void setMarble(int x, int y, Marble b) {
        fields[x][y] = b;
    }

    public Marble[][] getFields() {
        return fields;
    }

    public void setFields(Marble[][] fields) {
        this.fields = fields;
    }

    //@Override
    public Marble[][] copy() {
        Marble[][] output = new Marble[size][size];
        for (int i= 0; i< size; i ++) {
            for (int j = 0; j != size; j ++) {
                output[i][j] = fields[i][j];
            }
        }
        return output;
    }

}
