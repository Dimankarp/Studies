package mitya.haha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public final class Role {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Column(length = 60, unique = true)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private AccessLevel accessLevel;

}
