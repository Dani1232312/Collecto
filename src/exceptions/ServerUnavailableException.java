package exceptions;

public class ServerUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServerUnavailableException(String s) {
        super(s);
    }

}
