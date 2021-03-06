package ru.oxygensoftware.backoffice.data;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Dmitry Raguzin
 * Date: 12.11.15
 */

@Entity
public class State {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "state", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<User> users;

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        return getId().equals(state.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
