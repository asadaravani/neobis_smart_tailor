package kg.neobis.smarttailor.entity;

import jakarta.persistence.*;
import kg.neobis.smarttailor.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser extends BaseEntity implements UserDetails {

    @Column
    String imagePath;

    @Column
    String name;

    @Column
    String surname;

    @Column
    String patronymic;

    @Column(unique = true, nullable = false)
    String email;

    @Column
    String password;

    @Column
    String phoneNumber;

    @Column
    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToOne
    @JoinColumn
    Position employeePosition;

    @ManyToOne
    @JoinColumn
    Organization organization;

    @Column
    Boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
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
