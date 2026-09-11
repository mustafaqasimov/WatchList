package com.movie.watchlist.entity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.movie.watchlist.entity.base.BaseEntity;
import com.movie.watchlist.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    String password;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;
}
