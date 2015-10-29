package ru.oxygensoftware.backoffice.ui.view.invite;

import ru.oxygensoftware.backoffice.data.Product;

import java.util.Date;

/**
 * Created by dmitry on 27.10.15.
 */
public class InviteDto {
    private Date dateExpire;
    private Long amount;
    private Product product;
    private String comment;
    private Date dateCreated;

    public InviteDto() {
        dateExpire = new Date();
        dateCreated = new Date();
        amount = 0l;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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

    public Date getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Date dateExpire) {
        this.dateExpire = dateExpire;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
