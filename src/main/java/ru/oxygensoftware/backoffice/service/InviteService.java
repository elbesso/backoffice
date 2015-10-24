package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.Invite;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class InviteService {
    @PersistenceContext
    private EntityManager em;

    public Invite create() {
        Invite invite = new Invite();
        invite.setComment("test comment");
        invite.setDateCreated(new Date(System.currentTimeMillis()));
        return invite;
    }

    @Transactional
    public void save(Invite invite) {
        em.merge(invite);
    }

    public List<Invite> getAll() {
        return em.createQuery("FROM Invite", Invite.class).getResultList();
    }
}
