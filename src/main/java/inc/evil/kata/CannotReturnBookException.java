package inc.evil.kata;
public class CannotReturnBookException extends RuntimeException {

    public CannotReturnBookException(String message) {
        super(message);
    }
}
