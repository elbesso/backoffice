package ru.oxygensoftware.backoffice.service;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oxygensoftware.backoffice.data.SystemToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Service("tokenRepository")
public class ApplicationTokenRepository implements PersistentTokenRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        SystemToken t = new SystemToken();
        t.setUsername(token.getUsername());
        t.setSeries(token.getSeries());
        t.setToken(token.getTokenValue());
        t.setLastUsed(token.getDate());
        em.persist(t);
    }

    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        SystemToken t = em.find(SystemToken.class, series);
        if (t != null) {
            t.setToken(tokenValue);
            t.setLastUsed(lastUsed);
            em.persist(t);
        }
    }

    @Transactional
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        SystemToken t = em.find(SystemToken.class, seriesId);
        if (t != null) {
            return new PersistentRememberMeToken(t.getUsername(), t.getSeries(), t.getToken(), t.getLastUsed());
        }
        return null;
    }

    @Transactional
    public void removeUserTokens(String username) {
        List<SystemToken> list = em.createQuery("from SystemToken where username = :username", SystemToken.class)
                .setParameter("username", username).getResultList();
        list.forEach(em::remove);
    }
}
