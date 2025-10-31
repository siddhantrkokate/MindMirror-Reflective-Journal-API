package com.mindmirror.mindmirror_backend.repository;

import com.mindmirror.mindmirror_backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserId(String userId);
    void deleteByUserId(String userId);
}