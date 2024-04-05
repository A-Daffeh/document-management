package com.adoustar.documentmanagement.entity;

import com.adoustar.documentmanagement.enums.Authority;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "roles")
@JsonInclude(NON_DEFAULT)
public class RoleEntity extends Auditable {
    private String name;
    private Authority authorities;
}
