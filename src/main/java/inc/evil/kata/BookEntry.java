package inc.evil.kata;

import java.util.concurrent.atomic.AtomicInteger;

public class BookEntry {

    private final Book book;
    private final AtomicInteger numberOfCopies = new AtomicInteger();
    private final int initialNumberOfCopies;

    public BookEntry(Book book, int numberOfCopies) {
        this.book = book;
        this.numberOfCopies.set(numberOfCopies);
        this.initialNumberOfCopies = numberOfCopies;
    }

    public Book getBook() {
        return book;
    }

    public int getNumberOfCopies() {
        return numberOfCopies.get();
    }

    public boolean canBorrow() {
        return numberOfCopies.get() > 0;
    }

    public Book borrow() {
        int remainingCopies = numberOfCopies.get();
        if (remainingCopies < 1) {
            throw new CannotBorrowBookException("Cannot borrow the book because now copies are available.");
        }
        numberOfCopies.compareAndSet(remainingCopies, remainingCopies - 1);
        return book;
    }

    public void returnBook() {
        if (initialNumberOfCopies == numberOfCopies.get()) {
            throw new CannotReturnBookException("Cannot return the book because is appears that the library did not lent it");
        }
        numberOfCopies.incrementAndGet();
    }
}
