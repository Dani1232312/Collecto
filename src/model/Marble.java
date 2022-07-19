package model;

public enum Marble {
    /**
     * A blue marble.
     */
    BLUE("B", 1),

    /**
     * A orange marble.
     */
    ORANGE("O", 2),

    /**
     * A red marble.
     */
    RED("R", 3),

    /**
     * A yellow marble.
     */
    YELLOW("Y", 4),

    /**
     * A purple marble.
     */
    PURPLE("P",5),

    /**
     * A green marble.
     */
    GREEN("G",6),

    /**
     * No Marble.
     */
    EMPTY(" ", 0),

    /**
     * A Null Space (outside the board).
     */
    NULL("", -2);

    private String marble;
    private int index;
    public static final int COLOURS = 6;
    public static final int NR_MARBLES = 8;

    // -- Constructors -----------------------------------------------

    Marble(String s, int index) {
        marble = s;
        this.index = index;
    }

    // -- Queries ----------------------------------------------------

    /**
     * Return the index of a marble.
     *
     * @ensures /result >=-1 && /result <=4
     */
    public int getIndex() {
        return index;
    }

    public static Marble indexToMarble(int index) {
        for (Marble m : values()) {
            if (m.index == index) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the string representation of the marble.
     *
     * @ensures /result != null
     */
    @Override
    public String toString() {
        return marble;
    }


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_ORANGE = "";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String REMOVED = "\u001B[47m";

    public String getNumColor() {
        switch (this) {
            case GREEN:
                return ANSI_GREEN + "1" + ANSI_RESET;
            case BLUE:
                return ANSI_BLUE + "2" + ANSI_RESET;
            case PURPLE:
                return ANSI_PURPLE + "3" + ANSI_RESET;
            case RED:
                return ANSI_RED + "4" + ANSI_RESET;
            case ORANGE:
                return ANSI_ORANGE + "5" + ANSI_RESET;
            case YELLOW:
                return ANSI_YELLOW + "6" + ANSI_RESET;
        }
        return REMOVED + "0" + ANSI_RESET;
    }
}
