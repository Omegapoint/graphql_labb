package com.graphqljava.tutorial.bookdetails.components;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GraphQLDataFetchers {

    private static List<Map<String, String>> books = new ArrayList<>();

    private static List<Map<String, String>> authors = new ArrayList<>();

    static {
        authors.add(ImmutableMap.of("id", "author-1",
                "firstName", "Joanne",
                "lastName", "Rowling"));
        authors.add(ImmutableMap.of("id", "author-2",
                "firstName", "Herman",
                "lastName", "Melville"));
        authors.add(ImmutableMap.of("id", "author-3",
                "firstName", "Anne",
                "lastName", "Rice"));

        books.add(ImmutableMap.of("id", "book-1",
                "name", "Harry Potter and the Philosopher's Stone",
                "pageCount", "223",
                "authorId", "author-1"));
        books.add(ImmutableMap.of("id", "book-2",
                "name", "Moby Dick",
                "pageCount", "635",
                "authorId", "author-2"));
        books.add(ImmutableMap.of("id", "book-3",
                "name", "Interview with the vampire",
                "pageCount", "371",
                "authorId", "author-3"));
    }

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books
                    .stream()
                    .filter(book -> book.get("id").equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getBooks() {
        return dataFetchingEnvironment -> books;
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return authors
                    .stream()
                    .filter(author -> author.get("id").equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher addBook() {
        return dataFetchingEnvironment -> {
            Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
            Map<String, Object> bookArguments = (Map<String,Object>) arguments.get("book");
            Map<String, Object> authorArguments = (Map<String,Object>) bookArguments.get("author");

            String authorId;
            if(authorArguments == null){
                authorId = bookArguments.get("authorId").toString();
            }
            else {
                authorId = UUID.randomUUID().toString();
                ImmutableMap newAuthor = ImmutableMap.of(
                        "id", authorId,
                        "firstName", authorArguments.get("firstName").toString(),
                        "lastName", authorArguments.get("lastName").toString());
                authors.add(newAuthor);
            }
            ImmutableMap newBook = ImmutableMap.of(
                    "id", UUID.randomUUID().toString(),
                    "name", bookArguments.get("name").toString(),
                    "pageCount", bookArguments.get("pageCount").toString(),
                    "authorId", authorId);

            books.add(newBook);

            return books
                    .stream()
                    .filter(book -> book.get("id").equals(newBook.get("id")))
                    .findFirst()
                    .orElse(null);
        };
    }
}
