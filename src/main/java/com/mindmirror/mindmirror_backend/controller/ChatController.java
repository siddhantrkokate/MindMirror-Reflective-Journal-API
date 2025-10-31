package com.mindmirror.mindmirror_backend.controller;

import com.mindmirror.mindmirror_backend.model.Chat;
import com.mindmirror.mindmirror_backend.repository.ChatRepository;
import com.mindmirror.mindmirror_backend.repository.UserRepository;
import com.mindmirror.mindmirror_backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    // 1️⃣ Connect User
    @PostMapping("/connect")
    public Map<String, Object> connectUser(@RequestParam String userId) {
        var userOpt = userRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent()) {
            response.put("status", "connected");
            response.put("message", "Welcome back, " + userOpt.get().getName());
        } else {
            response.put("status", "new");
            response.put("message", "User not found. Please register or contact admin.");
        }

        return response;
    }

    // 2️⃣ Chat with AI (Gemini Integration)
    @PostMapping("/chat")
    public Chat chat(@RequestParam String userId, @RequestParam String message) {
        Chat chat = new Chat();
        chat.setUserId(userId);
        chat.setMessage(message);

        // Get Gemini AI response
        String aiResponse = geminiService.getAIResponse(message);
        chat.setResponse(aiResponse);

        // Save chat in DB
        return chatRepository.save(chat);
    }

    // 3️⃣ Get Chat History
    @GetMapping("/history/{userId}")
    public List<Chat> getHistory(@PathVariable String userId) {
        return chatRepository.findByUserId(userId);
    }

    // 4️⃣ Delete Chat History
    @DeleteMapping("/delete/{userId}")
    public String deleteHistory(@PathVariable String userId) {
        chatRepository.deleteByUserId(userId);
        return "✅ Chat history deleted for user: " + userId;
    }

    // 5️⃣ Update User Info (Optional enhancement)
    @PutMapping("/updateUser/{userId}")
    public Map<String, Object> updateUser(@PathVariable String userId, @RequestParam String newName) {
        var userOpt = userRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent()) {
            var user = userOpt.get();
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