package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    public Product create(String name) {
        Product result = create();
        result.setName(name);
        return result;
    }

    public Product create() {
        return new Product();
    }

    @Transactional
    public Product save(Product product) {
        return em.merge(product);
    }


    public List<Product> getAll() {
        return em.createQuery("FROM Product", Product.class).getResultList();
    }

    @Transactional
    public void delete(Integer id) {
        em.remove(get(id));
    }

    public Product get(Integer id) {
        return em.find(Product.class, id);
    }
}
