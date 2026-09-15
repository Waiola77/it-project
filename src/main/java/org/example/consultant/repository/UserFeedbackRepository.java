package org.example.consultant.repository;

import org.example.consultant.model.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    List<UserFeedback> findByUserId(String userId);

    List<UserFeedback> findByUserIdAndFeedbackType(String userId, String feedbackType);
}