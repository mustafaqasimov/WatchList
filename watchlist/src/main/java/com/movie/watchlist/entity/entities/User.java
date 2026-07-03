package com.movie.watchlist.entity.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.movie.watchlist.entity.base.BaseEntity;
import com.movie.watchlist.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    String username;

    @NotBlank
    @Column(unique = true,nullable = false)
    @JsonIgnore
    String password;

    @NotBlank
    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;
}
