package com.distribuida.Authors.authors.repo;

import com.distribuida.books.authors.db.Author;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class AuthorRepository{

    @Inject
    private EntityManager entityManager;

    public void create(Author author) {
        entityManager.persist(author);
    }

    public void update(Author author) {
        entityManager.merge(author);
    }

    public void delete(Integer id) {
        Author author = entityManager.find(Author.class, id);
        if (author != null) {
            entityManager.remove(author);
        }
    }

    public Author findById(Integer id) {
        return entityManager.find(Author.class, id);
    }

    public List<Author> findAll() {
        return entityManager.createQuery("SELECT b FROM Author b", Author.class)
                .getResultList();
    }

}
