package org.example.consultant.controller;

import org.example.consultant.aiservices.BookRecommendationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookRecommendationController {
    @Autowired
    private BookRecommendationServices bookServices;

    @RequestMapping("/chat")
    public String chat(String message) {
        String result = bookServices.chat(message);
        return result;
    }
}
