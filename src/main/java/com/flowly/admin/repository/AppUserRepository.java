package com.flowly.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.flowly.shared.model.User;

public interface AppUserRepository extends JpaRepository<User, UUID> {
}

