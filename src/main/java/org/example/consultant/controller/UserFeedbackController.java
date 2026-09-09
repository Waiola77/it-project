package org.example.consultant.controller;

import org.example.consultant.model.UserFeedback;
import org.example.consultant.service.UserFeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFeedbackController {

    private final UserFeedbackService feedbackService;

    public UserFeedbackController(UserFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
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
}