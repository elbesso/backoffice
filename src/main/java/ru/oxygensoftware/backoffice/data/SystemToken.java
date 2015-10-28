package ru.oxygensoftware.backoffice.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class SystemToken {
    @Id
    @Column(name = "series")
    private String series;

    @Column(name = "username")
    private String username;

    @Column(name = "token")
    private String token;

    @Column(name = "last_used")
    private Date lastUsed;

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String value) {
        this.series = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date value) {
        this.lastUsed = value;
    }

}
