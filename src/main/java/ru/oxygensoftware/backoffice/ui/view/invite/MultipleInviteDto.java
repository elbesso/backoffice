package ru.oxygensoftware.backoffice.ui.view.invite;

import ru.oxygensoftware.backoffice.data.Invite;
import ru.oxygensoftware.backoffice.data.Product;

import java.util.Date;
import java.util.Set;

/**
 * Created by Dmitry Raguzin
 * Date: 19.11.15
 */
public class MultipleInviteDto {
    private Set<Invite> invites;
    private Product product;
    private Date dateExpire;
    private String comment;

    public MultipleInviteDto(Set<Invite> invites) {
        this.invites = invites;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Date dateExpire) {
        this.dateExpire = dateExpire;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<Invite> getInvites() {
        return invites;
    }

    public void setInvites(Set<Invite> invites) {
        this.invites = invites;
    }
}
