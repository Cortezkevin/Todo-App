package com.kevin.todo_app.security.model;

import com.kevin.todo_app.documents.User;
import com.kevin.todo_app.enums.RolName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class MainUser implements UserDetails {

    private String username;
    @Getter
    private String email;
    private String password;
    @Getter @Setter
    private List<RolName> roles;

    public static MainUser build(User user){
        return new MainUser(user.getEmail(), user.getEmail(), user.getPassword(), user.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(RolName::name).map( SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
