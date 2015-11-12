package ru.oxygensoftware.backoffice.data;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Dmitry Raguzin
 * Date: 12.11.15
 */

@Entity
public class Country {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "country", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<User> users;

    @OneToMany(mappedBy = "country", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<State> state;

    public Set<State> getState() {
        return state;
    }

    public void setState(Set<State> state) {
        this.state = state;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;

        Country country = (Country) o;

        return getId().equals(country.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
