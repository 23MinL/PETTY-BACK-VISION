package io.github.petty.users.repository;

import io.github.petty.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Boolean existsByUsername(String username);

    Users findByUsername(String username);
}
