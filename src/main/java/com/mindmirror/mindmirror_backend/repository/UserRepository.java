package com.mindmirror.mindmirror_backend.repository;

import com.mindmirror.mindmirror_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}