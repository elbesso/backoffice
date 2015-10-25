package ru.oxygensoftware.backoffice.data;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by dmitry on 25.10.15.
 */

@Entity
public class Product {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "product")
    private Set<Invite> invites;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Invite> getInvites() {
        return invites;
    }

    public void setInvites(Set<Invite> invites) {
        this.invites = invites;
    }
}
