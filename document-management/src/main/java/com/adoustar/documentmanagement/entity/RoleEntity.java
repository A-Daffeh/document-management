package com.adoustar.documentmanagement.entity;

import com.adoustar.documentmanagement.enums.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "roles")
public class RoleEntity extends Auditable {
    private String name;
    private Authority authority;
}
