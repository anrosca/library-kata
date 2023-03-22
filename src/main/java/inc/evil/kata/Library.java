package inc.evil.kata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Library {

    private final Map<String, BookEntry> catalog = new HashMap<>();
    private final Map<String, List<Book>> borrowedBooks = new HashMap<>();

    public void addBookToCatalog(BookEntry bookEntry) {
        catalog.put(bookEntry.getBook().isbn(), bookEntry);
    }

    public Optional<Book> borrowBook(String userId, String bookIsbn) {
        if (catalog.containsKey(bookIsbn)) {
            BookEntry bookEntry = catalog.get(bookIsbn);
            if (bookEntry.canBorrow()) {
                Book borrowedBook = bookEntry.borrow();
                borrowedBooks.computeIfAbsent(userId, userBooks -> new ArrayList<>()).add(bookEntry.getBook());
                return Optional.of(borrowedBook);
            }
        }
        return Optional.empty();
    }

    public boolean isBookAvailable(String bookIsbn) {
        return catalog.containsKey(bookIsbn) && catalog.get(bookIsbn).canBorrow();
    }

    public void returnBook(String userId, String bookIsbn) {
        if (catalog.containsKey(bookIsbn)) {
            BookEntry bookEntry = catalog.get(bookIsbn);
            if (borrowedBooks.containsKey(userId) && hasBorrowedBook(userId, bookIsbn)) {
                bookEntry.returnBook();
                borrowedBooks.get(userId).remove(bookEntry.getBook());
                return;
            }
        }
        throw new CannotReturnBookException("Cannot return the book because is appears that the library did not lent it");
    }

    public List<Book> getBorrowedBooksFor(String userId) {
        if (borrowedBooks.containsKey(userId)) {
            List<Book> books = borrowedBooks.get(userId);
            return Collections.unmodifiableList(books);
        }
        return List.of();
    }

    private boolean hasBorrowedBook(String userId, String bookIsbn) {
        return borrowedBooks.getOrDefault(userId, List.of())
                            .stream()
                            .anyMatch(book -> book.isbn().equals(bookIsbn));
    }
}
