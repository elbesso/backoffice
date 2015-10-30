package ru.oxygensoftware.backoffice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.oxygensoftware.backoffice.data.SystemUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SystemUser> list = em.createQuery("FROM SystemUser " +
                "WHERE username = :username", SystemUser.class)
                .setParameter("username", username).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        throw new UsernameNotFoundException(username);
    }
}
