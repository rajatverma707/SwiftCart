package com.rv.admin.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "role")
public class Role{
	
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer roleId;

private String roleName;

private LocalDateTime date_created;
private LocalDateTime last_updated;
}