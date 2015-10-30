package ru.oxygensoftware.backoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.SystemUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Dmitry Raguzin
 * Date: 30.10.15
 */
@Service
public class SystemUserService  {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private PasswordEncoder encoder;

    public SystemUser create() {
        return new SystemUser();
    }

    public List<SystemUser> getAll() {
        return em.createQuery("FROM SystemUser", SystemUser.class).getResultList();
    }

    @Transactional
    public void delete(Integer id) {
        em.remove(get(id));
    }

    public SystemUser get(Integer id) {
        return em.find(SystemUser.class, id);
    }

    @Transactional
    public SystemUser save(SystemUser systemUser) {
        systemUser.setPassword(encoder.encode(systemUser.getPassword()));
        return em.merge(systemUser);
    }
}
