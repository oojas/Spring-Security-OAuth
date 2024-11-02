package com.spring.authentication.Modals;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder // build the object in easy way using design pattern builder
@Entity
@Table(name = "auth_user")
public class User implements UserDetails {
    /*
    * User Details is basically an interface which primarily deals with Spring Security and authentication and password.
    * Whenever we try to create a user class which will be used for Authentication we have to implement the UserDetails interface.
    * It basically acts as a Credential Manager.
    * Implementations are not used directly by Spring Security for security purposes. They simply store user information which is later encapsulated into
    * Authentication objects.
    * This allows non-security related user information (such as email addresses, telephone numbers etc) to be stored in a convenient location
    * */
    @GeneratedValue
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    @Column(
            unique = true
    )
    private String email;
    private String pass;
    @Enumerated(EnumType.STRING) // by default the enum type is ORDINAL which means it will take integers.
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return pass;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
