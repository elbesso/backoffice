package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager em;

    public User create() {
        User result = new User();
        return result;
    }

    public List<User> getAll() {
        return em.createQuery("FROM User", User.class).getResultList();
    }

    @Transactional
    public void delete(Integer id) {
        em.remove(get(id));
    }

    public User get(Integer id) {
        return em.find(User.class, id);
    }

    @Transactional
    public User save(User user) {
        return em.merge(user);
    }
}
