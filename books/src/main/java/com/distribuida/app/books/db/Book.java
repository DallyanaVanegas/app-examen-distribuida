package com.distribuida.app.books.db;

import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "books")
@Schema(name="Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Schema(required = true, description = "Book id")
    private Integer id;

    @Column
    @Schema(required = true, description = "Book ISBN")
    private String isbn;

    @Column
    @Schema(required = true, description = "Book Title")
    private String title;

    @Column
    @Schema(required = true, description = "Book Price")
    private Double price;

    @Column(name="author_id")
    @Schema(required = true, description = "Book Author ID")
    private Integer authorId;

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Book(){}

    public Book(String name){
        this.isbn = name;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", authorId=" + authorId +
                '}';
    }
}
