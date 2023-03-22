package inc.evil.kata;

public class CannotBorrowBookException extends RuntimeException {

    public CannotBorrowBookException(String message) {
        super(message);
    }
}
