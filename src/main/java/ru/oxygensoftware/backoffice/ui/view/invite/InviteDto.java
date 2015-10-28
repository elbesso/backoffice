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

    public InviteDto() {
        dateExpire = new Date();
        amount = 0l;
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
