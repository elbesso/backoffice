package ru.oxygensoftware.backoffice.service;

import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.Country;
import ru.oxygensoftware.backoffice.data.State;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Dmitry Raguzin
 * Date: 12.11.15
 */

@Service
public class CountryService {
    @PersistenceContext
    private EntityManager em;

    public List<State> getAllStates() {
        return em.createQuery("FROM State", State.class).getResultList();
    }

    public List<Country> getAllCountries() {
        return em.createQuery("FROM Country", Country.class).getResultList();
    }
}
