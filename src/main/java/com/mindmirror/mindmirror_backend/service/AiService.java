package com.mindmirror.mindmirror_backend.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AiService {

    private static final String[] MOCK_RESPONSES = {
            "That’s an interesting thought.",
            "Can you tell me more about that?",
            "Hmm... let me think about it.",
            "I get what you mean. Why do you think that is?",
            "That’s a smart observation!"
    };

    public String generateResponse(String userMessage) {
        Random random = new Random();
        String base = MOCK_RESPONSES[random.nextInt(MOCK_RESPONSES.length)];
        return "MindMirror AI: " + base + " (You said: \"" + userMessage + "\")";
    }
}