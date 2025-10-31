package com.mindmirror.mindmirror_backend.controller;

import com.mindmirror.mindmirror_backend.model.Chat;
import com.mindmirror.mindmirror_backend.model.User;
import com.mindmirror.mindmirror_backend.repository.ChatRepository;
import com.mindmirror.mindmirror_backend.repository.UserRepository;
import com.mindmirror.mindmirror_backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows requests from anywhere
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    // ================= Connect User =================
    @PostMapping("/connect")
    public Map<String, Object> connectUser(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        Map<String, Object> response = new HashMap<>();

        if (userId == null || userId.isEmpty()) {
            response.put("status", "error");
            response.put("message", "userId is required in request body");
            return response;
        }

        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            response.put("status", "connected");
            response.put("message", "Welcome back, " + userOpt.get().getName());
        } else {
            response.put("status", "new");
            response.put("message", "User not found. Please register or contact admin.");
        }

        return response;
    }

    // ================= Chat with AI =================
    @PostMapping("/chat")
    public Chat chat(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String message = payload.get("message");

        if (userId == null || message == null) {
            throw new IllegalArgumentException("userId and message are required in request body");
        }

        Chat chat = new Chat();
        chat.setUserId(userId);
        chat.setMessage(message);

        // Get AI response
        String aiResponse = geminiService.getAIResponse(message);
        chat.setResponse(aiResponse);

        // Save chat
        return chatRepository.save(chat);
    }

    // ================= Chat History =================
    @GetMapping("/history/{userId}")
    public List<Chat> getHistory(@PathVariable String userId) {
        return chatRepository.findByUserId(userId);
    }

    // ================= Delete Chat History =================
    @DeleteMapping("/delete/{userId}")
    public Map<String, String> deleteHistory(@PathVariable String userId) {
        chatRepository.deleteByUserId(userId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "deleted");
        response.put("message", "Chat history deleted for user: " + userId);
        return response;
    }

    // ================= Update User Info =================
    @PutMapping("/updateUser/{userId}")
    public Map<String, Object> updateUser(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String newName = payload.get("newName");
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(newName);
            userRepository.save(user);

            response.put("status", "updated");
            response.put("message", "User name updated successfully to: " + newName);
        } else {
            response.put("status", "failed");
            response.put("message", "User not found. Cannot update.");
        }

        return response;
    }
}