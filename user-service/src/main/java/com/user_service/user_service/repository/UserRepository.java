package com.user_service.user_service.repository;

import com.user_service.user_service.models.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<UserEntity> findByUsernameIn(List<String> usernames);

}
