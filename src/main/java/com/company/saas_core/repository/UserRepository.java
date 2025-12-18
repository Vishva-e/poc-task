package com.company.saas_core.repository;

import com.company.saas_core.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    @Query("select u from users u where u.username = :username")
    Optional<UserAccount> findByUsername(@Param("username") String username);
}
