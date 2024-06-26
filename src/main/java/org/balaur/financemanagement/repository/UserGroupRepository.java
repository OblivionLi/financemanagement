package org.balaur.financemanagement.repository;

import org.balaur.financemanagement.model.user.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Query("SELECT ug FROM UserGroup ug WHERE ug.code = :code")
    Optional<UserGroup> findByCode(@Param("code") String code);
}
