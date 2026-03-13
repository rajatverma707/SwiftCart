package com.rv.auth.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "ROLES_DTLS")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer roleId;

    @Column
    private String name;
}
