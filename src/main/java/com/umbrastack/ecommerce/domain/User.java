package com.umbrastack.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umbrastack.ecommerce.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_user")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String phone;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.CLIENT;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

    // Construtor sem a coleção de pedidos, mantendo a regra de segurança
    public User(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userRole = UserRole.CLIENT;
    }

    public User(Long id, String name, String email, String phone, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userRole = role != null ? role : UserRole.CLIENT;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        if (this.userRole == UserRole.ADMIN){
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_CLIENT")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    @Override
    public String getUsername() {
        return this.email;
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
