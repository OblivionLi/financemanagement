package org.balaur.financemanagement.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "principle_groups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @ManyToMany(mappedBy = "userGroups")
    private Set<User> users = new HashSet<>();

    @Override
    public String toString() {
        return "UserGroup{" +
                "code='" + code + '\'' +
                '}';
    }
}
