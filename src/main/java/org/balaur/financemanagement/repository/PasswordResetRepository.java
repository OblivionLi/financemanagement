package org.balaur.financemanagement.repository;

import jakarta.websocket.server.PathParam;
import org.balaur.financemanagement.model.user.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    @Query("SELECT r FROM PasswordReset r WHERE r.token = :token")
    PasswordReset findByToken(@PathParam("token") String token);
}