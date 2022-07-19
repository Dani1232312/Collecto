package model.player;

import controller.Game;
import model.server.CollectoServer;
import model.server.Reader;

public class HumanPlayer extends Player{
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public int[] getMove(Game game) throws NumberFormatException{
        System.out.println("Please make a move!");
        System.out.println("<number> for a single move | <number>~<number> for a double move");

        String input = Reader.readString();
        int[] out =new int[input.length()+2];
        String[] split = input.split("~");
        if(split.length>1) {
            out[0] = 2;
            try {
                if (split[0].length() > 1) {
                    out[1] = Integer.parseInt(String.valueOf(split[0].charAt(0)));
                    out[2] = Integer.parseInt(String.valueOf(split[0].charAt(1)));
                } else {
                    out[1] = 0;
                    out[2] = Integer.parseInt(String.valueOf(split[0].charAt(0)));
                }
                CollectoServer.convert(out, split);
            }catch (NumberFormatException e){
                System.out.println("You need to input numbers to make a move!");
            }
        }
        else {
            try {
                CollectoServer.convert2(out, split);
            }catch (NumberFormatException e){
                System.out.println("You need to input numbers to make a move!");
            }
        }


        return out;
    }
}
