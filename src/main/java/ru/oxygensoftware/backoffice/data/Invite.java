package ru.oxygensoftware.backoffice.data;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Invite {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "invite")
    private String invite;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "date_expire")
    private Date dateExpire;

    @Column(name = "date_activated")
    private Date dateActivated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "comment")
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Date dateExpire) {
        this.dateExpire = dateExpire;
    }

    public Date getDateActivated() {
        return dateActivated;
    }

    public void setDateActivated(Date dateActivated) {
        this.dateActivated = dateActivated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invite)) return false;

        Invite invite1 = (Invite) o;

        return invite.equals(invite1.invite);

    }

    @Override
    public int hashCode() {
        return invite.hashCode();
    }
}
