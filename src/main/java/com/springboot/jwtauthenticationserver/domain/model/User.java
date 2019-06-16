package com.springboot.jwtauthenticationserver.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    private String username;

    @ElementCollection(targetClass=Long.class)
    private List<Long> playlist;

    @NaturalId
    private String email;

    @JsonIgnore
    private String password;

    private boolean enabled = true;
    private boolean expired = false;
    private boolean locked = false;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Embedded
    private DateAudit date = new DateAudit(Instant.now(), Instant.now());

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @JsonIgnore
    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public User() {}

    public User(String username, String email, String password, List<Long> playlist) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.playlist = playlist;
    }

    public List<Long> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Long> playlist) {
        this.playlist = playlist;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DateAudit getDate() {
        return date;
    }

    public void setDate(DateAudit date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Username: " + getUsername() + " " +
                "Email: " + getEmail() + " " +
                "Password: " + getPassword() + " " +
                "Playlist: " + getPlaylist() + " " +
                "Roles: " + getRoles().toString() + " " +
                "CreatedAt: " + getDate().getCreatedAt().toString() + " " +
                "UpdatedAt: " + getDate().getUpdatedAt().toString();
    }
}
