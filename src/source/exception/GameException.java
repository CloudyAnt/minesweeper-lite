package source.exception;

public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }

    public GameException(Throwable t) {
        super(t);
    }
}
