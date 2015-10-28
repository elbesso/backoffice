package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class InviteService {
    @PersistenceContext
    private EntityManager em;

    public Invite create(Date expire, Product product, String comment) {
        Invite result = new Invite();
        result.setDateCreated(new Date(System.currentTimeMillis()));
        result.setDateExpire(expire);
        result.setInvite(generateInviteString());
        result.setProduct(product);
        result.setComment(comment);
        return result;
    }

    @Transactional
    public void save(Invite invite) {
        em.merge(invite);
    }

    public List<Invite> getAll() {
        return em.createQuery("FROM Invite", Invite.class).getResultList();
    }

    @Transactional
    public void generateInvites(Long amount, Product product, Date expire, String comment) {
        final Set<Invite> initialSet = new HashSet<>(getAll());
        final Set<Invite> result = new HashSet<>(initialSet);
        boolean contains = false;
        for (long i = 0; i < amount; i++) {
            while (!contains) {
                contains = result.add(create(expire, product, comment));
            }
            contains = false;
        }
        result.removeAll(initialSet);
        result.forEach(this::save);
    }

    private String generateInviteString() {
        final char[] symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        Random generator = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                sb.append(symbols[generator.nextInt(symbols.length)]);
            }
            if (i != 3) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    @Transactional
    public void delete(Integer id) {
        em.remove(get(id));
    }

    public Invite get(Integer id) {
        return em.find(Invite.class, id);
    }
}
