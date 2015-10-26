package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    public Product create(String name) {
        Product result = new Product();
        result.setName(name);
        return result;
    }

    @Transactional
    public Product save(Product product) {
        return em.merge(product);
    }
}
