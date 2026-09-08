package org.example.consultant.controller;

import org.example.consultant.service.BookChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookRecommendationController {
    @Autowired
    private BookChatService bookChatService;

    @RequestMapping("/chat")
    public String chat(String message) {
        return bookChatService.chat(message);
    }
}