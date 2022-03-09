package io.jzheaux.springsecurity.resolutions;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity(name="users")
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    UUID id;

    @Column
    String username;

    @Column
    String password;

    @Column
    boolean enabled = true;

    User() {}
    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.enabled = user.enabled;
        this.userAuthorities = user.userAuthorities;
    }
    public User(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
    }

    @OneToMany(fetch= FetchType.EAGER, cascade= CascadeType.ALL)
    Collection<UserAuthority> userAuthorities = new ArrayList<>();

    public Collection<UserAuthority> getUserAuthorities() {
        return Collections.unmodifiableCollection(this.userAuthorities);
    }

    public void grantAuthority(String authority) {
        UserAuthority userAuthority = new UserAuthority(this, authority);
        this.userAuthorities.add(userAuthority);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
