package ru.oxygensoftware.backoffice.data;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
public class SystemRole implements GrantedAuthority {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "rolename")
    @Enumerated(EnumType.STRING)
    private SystemRoleEnum rolename;

    @Override
    public String getAuthority() {
        return rolename.toString();
    }

    public SystemRoleEnum getRolename() {
        return rolename;
    }

    public void setRolename(SystemRoleEnum value) {
        this.rolename = value;
    }
}
