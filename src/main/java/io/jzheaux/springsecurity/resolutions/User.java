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

    @Column(name="full_name")
    String fullName;

    @Column
    String password;

    @Column
    boolean enabled = true;

    @Column
    String subscription;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Collection<User> friends = new ArrayList<>();

    User() {}
    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.enabled = user.enabled;
        this.userAuthorities = user.userAuthorities;
        this.fullName = user.fullName;
        this.subscription = user.subscription;
        this.friends = user.friends;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public Collection<User> getFriends() {
        return friends;
    }
    public void addFriend(User friend) {
        friends.add(friend);
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
