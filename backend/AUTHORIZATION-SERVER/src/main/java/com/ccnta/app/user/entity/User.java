package com.ccnta.app.user.entity;

import com.ccnta.app.image.Image;
import com.ccnta.app.role.entity.Role;
import com.ccnta.app.shared.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_users")
@NamedQueries(
        {
                @NamedQuery(name = "User.findByUsername", query = "SELECT U FROM User U WHERE U.username=:username"),
                @NamedQuery(name = "User.findByUserId", query = "SELECT U FROM User U WHERE U.userId=:userId"),
                @NamedQuery(name = "User.findByEmail", query = "SELECT U FROM User U WHERE U.email=:email")
        }
)
@ToString
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_user_id_seq_generator")
    @SequenceGenerator(name = "_user_id_seq_generator", sequenceName = "_user_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    private String userId;
    private String firstName;
    private String lastName;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    private String email;
    private String password;

    @Transient
    private String confirmPassword;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Image profile;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

}
