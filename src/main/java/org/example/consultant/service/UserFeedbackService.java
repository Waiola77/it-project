package org.example.consultant.service;

import org.example.consultant.model.UserFeedback;
import org.example.consultant.repository.UserFeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFeedbackService {

    private final UserFeedbackRepository feedbackRepository;

    public UserFeedbackService(UserFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public UserFeedback recordFeedback(String userId,
                                       String bookId,
                                       String feedbackType) {

        UserFeedback feedback =
                new UserFeedback(userId, bookId, feedbackType);

        return feedbackRepository.save(feedback);
    }

    public List<UserFeedback> getFeedbackForUser(String userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public List<UserFeedback> getRejectedBooks(String userId) {
        return feedbackRepository.findByUserIdAndFeedbackType(
                userId,
                "REJECTED"
        );
    }
}