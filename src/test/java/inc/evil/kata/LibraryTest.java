package inc.evil.kata;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LibraryTest {

    private final Library library = new Library();

    private final Book thinkingInJava = Book.builder()
                                            .isbn("0987654321")
                                            .title("Thinking in Java")
                                            .authors(Set.of(new Author("Bruce Eckel")))
                                            .publishYear(2006)
                                            .publisher("Manning")
                                            .build();
    private final Book javaConcurrencyInPractice = Book.builder()
                                                       .isbn("0321349601")
                                                       .title("Thinking in Java")
                                                       .authors(Set.of(new Author("Brian Gotez"), new Author("Joshua Bloch"), new Author("David Holmes")))
                                                       .publishYear(2006)
                                                       .publisher("Addison-Wesley")
                                                       .build();

    @Test
    void shouldBeAbleToAddBooksToLibrary() {
        String bookIsbn = "0987654321";

        library.addBookToCatalog(new BookEntry(thinkingInJava, 5));

        assertThat(library.isBookAvailable(bookIsbn)).isTrue();
    }

    @Test
    void whenBookIsNotInCatalog_itShouldNoBeAvailable() {
        library.addBookToCatalog(new BookEntry(thinkingInJava, 5));

        assertThat(library.isBookAvailable("111")).isFalse();
    }

    @Test
    void whenBookIsNotInCatalog_borrowReturnsNothing() {
        library.addBookToCatalog(new BookEntry(thinkingInJava, 5));

        Optional<Book> book = library.borrowBook("user1", "1917654321");
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    void whenBookIsPresentInCatalog_andItHasAvailableCopies_borrowReturnsIt() {
        String bookIsbn = "0987654321";

        library.addBookToCatalog(new BookEntry(thinkingInJava, 1));

        Optional<Book> book = library.borrowBook("user1", bookIsbn);
        assertThat(book).isEqualTo(Optional.of(thinkingInJava));
    }

    @Test
    void whenThereAre3CopiesOfTheSameBook_shouldBeAbleToBorrowIt3Times() {
        String bookIsbn = "0987654321";

        library.addBookToCatalog(new BookEntry(thinkingInJava, 3));

        Optional<Book> firstBook = library.borrowBook("user1", bookIsbn);
        Optional<Book> secondBook = library.borrowBook("user1", bookIsbn);
        Optional<Book> thirdBook = library.borrowBook("user1", bookIsbn);

        assertThat(firstBook).isEqualTo(Optional.of(thinkingInJava));
        assertThat(secondBook).isEqualTo(Optional.of(thinkingInJava));
        assertThat(thirdBook).isEqualTo(Optional.of(thinkingInJava));
    }

    @Test
    void whenThereAre2CopiesOfTheSameBook_shouldBeAbleToBorrowIt2TimesAtMost() {
        String bookIsbn = "0987654321";

        library.addBookToCatalog(new BookEntry(thinkingInJava, 2));

        Optional<Book> firstBook = library.borrowBook("user1", bookIsbn);
        Optional<Book> secondBook = library.borrowBook("user1", bookIsbn);
        Optional<Book> thirdBook = library.borrowBook("user1", bookIsbn);

        assertThat(firstBook).isEqualTo(Optional.of(thinkingInJava));
        assertThat(secondBook).isEqualTo(Optional.of(thinkingInJava));
        assertThat(thirdBook).isEqualTo(Optional.empty());
    }

    @Test
    void whenBookWasNotLent_shouldNotBeAbleToReturnIt() {
        String bookIsbn = "0987654321";
        String expectedMessage = "Cannot return the book because is appears that the library did not lent it";

        assertThatThrownBy(() -> library.returnBook("user1", bookIsbn))
            .hasMessage(expectedMessage)
            .isInstanceOf(CannotReturnBookException.class);
    }

    @Test
    void whenThereAre2CopiesOfTheSameBook_shouldNotBeAbleToReturnIt3Times() {
        String bookIsbn = "0987654321";
        String expectedMessage = "Cannot return the book because is appears that the library did not lent it";

        library.addBookToCatalog(new BookEntry(thinkingInJava, 2));
        library.borrowBook("user1", bookIsbn);
        library.borrowBook("user1", bookIsbn);

        library.returnBook("user1", bookIsbn);
        library.returnBook("user1", bookIsbn);
        assertThatThrownBy(() -> library.returnBook("user1", bookIsbn))
            .hasMessage(expectedMessage)
            .isInstanceOf(CannotReturnBookException.class);
    }

    @Test
    void shouldBeAbleToGetBooksBorrowedByAParticularUser() {
        String thinkingInJavaIsbn = "0987654321";
        String javaConcurrencyInPracticeBookIsbn = "0321349601";
        String userId = "user1";
        library.addBookToCatalog(new BookEntry(thinkingInJava, 2));
        library.addBookToCatalog(new BookEntry(javaConcurrencyInPractice, 1));

        library.borrowBook(userId, thinkingInJavaIsbn);
        library.borrowBook(userId, javaConcurrencyInPracticeBookIsbn);

        List<Book> actualBorrowedBooks = library.getBorrowedBooksFor(userId);

        assertThat(actualBorrowedBooks).isEqualTo(List.of(thinkingInJava, javaConcurrencyInPractice));
    }
}
