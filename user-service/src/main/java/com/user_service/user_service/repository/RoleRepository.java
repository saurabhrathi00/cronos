package com.user_service.user_service.repository;

import com.user_service.user_service.models.dao.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity,String> {
}
