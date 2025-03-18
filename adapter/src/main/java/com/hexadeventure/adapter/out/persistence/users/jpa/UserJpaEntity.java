package com.hexadeventure.adapter.out.persistence.users.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserJpaEntity {
    @Id
    private String id;
    private String email;
    private String username;
    private String password;
    private String mapId;
}
