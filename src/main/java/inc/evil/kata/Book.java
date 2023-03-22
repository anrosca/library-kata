package inc.evil.kata;

import java.util.Set;

import lombok.Builder;

@Builder
public record Book(String isbn, String title, Set<Author> authors, int publishYear, String publisher) {
}
