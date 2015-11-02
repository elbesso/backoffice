package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.SystemRole;
import ru.oxygensoftware.backoffice.data.SystemRoleEnum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by dmitry on 02.11.15.
 */
@Service
public class SystemRoleService {
    @PersistenceContext
    private EntityManager em;

    public SystemRole getByName(SystemRoleEnum role) {
        return em.createQuery("FROM SystemRole " +
                "WHERE rolename = :role", SystemRole.class)
                .setParameter("role", role).getResultList().get(0);
    }
}
