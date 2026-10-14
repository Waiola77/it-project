package org.example.consultant.controller;

import org.example.consultant.model.UserFeedback;
import org.example.consultant.service.UserFeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.example.consultant.model.Book;
import org.example.consultant.service.UserPreferenceService;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
public class UserFeedbackController {

    private final UserFeedbackService feedbackService;
    private final UserPreferenceService preferenceService;

    public UserFeedbackController(
            UserFeedbackService feedbackService,
            UserPreferenceService preferenceService) {

        this.feedbackService = feedbackService;
        this.preferenceService = preferenceService;
    }

    @PostMapping("/feedback/reject")
    public UserFeedback rejectBook(
            @RequestParam String userId,
            @RequestParam String bookId) {

        return feedbackService.recordFeedback(
                userId,
                bookId,
                "REJECTED"
        );
    }

    @PostMapping("/feedback/like")
    public UserFeedback likeBook(
            @RequestParam String userId,
            @RequestParam String bookId) {

        return feedbackService.recordFeedback(
                userId,
                bookId,
                "LIKED"
        );
    }

    @GetMapping("/feedback/liked-books")
    public List<Book> getLikedBooks(@RequestParam String userId) {
        return preferenceService.getLikedBooks(userId);
    }

    @GetMapping("/feedback/rejected-books")
    public List<Book> getRejectedBooks(@RequestParam String userId) {
        return preferenceService.getRejectedBooks(userId);
    }
}