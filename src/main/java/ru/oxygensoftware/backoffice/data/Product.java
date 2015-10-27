package ru.oxygensoftware.backoffice.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by dmitry on 25.10.15.
 */

@Entity
public class Product extends SystemObject {
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Invite> invites;

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
