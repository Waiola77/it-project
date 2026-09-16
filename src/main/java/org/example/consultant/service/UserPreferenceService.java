package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.UserFeedback;
import org.example.consultant.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.example.consultant.model.DemoUserProfile;

import java.util.List;

@Service
public class UserPreferenceService {

    private final UserFeedbackService feedbackService;
    private final BookRepository bookRepository;
    private final DemoUserProfileService demoUserProfileService;

    public UserPreferenceService(
            UserFeedbackService feedbackService,
            BookRepository bookRepository,
            DemoUserProfileService demoUserProfileService) {

        this.feedbackService = feedbackService;
        this.bookRepository = bookRepository;
        this.demoUserProfileService = demoUserProfileService;
    }

    public List<Book> getLikedBooks(String userId) {

        List<UserFeedback> feedback =
                feedbackService.getFeedbackForUser(userId);

        return feedback.stream()
                .filter(f -> "LIKED".equals(f.getFeedbackType()))
                .map(f -> bookRepository.findById(f.getBookId()).orElse(null))
                .filter(book -> book != null)
                .toList();
    }

    public List<Book> getRejectedBooks(String userId) {

        List<UserFeedback> feedback =
                feedbackService.getFeedbackForUser(userId);

        return feedback.stream()
                .filter(f -> "REJECTED".equals(f.getFeedbackType()))
                .map(f -> bookRepository.findById(f.getBookId()).orElse(null))
                .filter(book -> book != null)
                .toList();
    }

    public String buildPreferenceContext(String userId) {

        List<Book> likedBooks = getLikedBooks(userId);
        List<Book> rejectedBooks = getRejectedBooks(userId);

        StringBuilder context = new StringBuilder();

        demoUserProfileService.getProfileById(userId).ifPresent(profile -> {
            context.append("Demo user profile:\n");

            if (profile.getStatedPreferences() != null
                    && !profile.getStatedPreferences().isBlank()) {
                context.append("Stated preferences: ")
                        .append(profile.getStatedPreferences())
                        .append("\n");
            }

            if (profile.getFavoriteBookTitles() != null
                    && !profile.getFavoriteBookTitles().isEmpty()) {
                context.append("Favorite books: ")
                        .append(String.join(", ", profile.getFavoriteBookTitles()))
                        .append("\n");
            }

            if (profile.getLikedAuthors() != null
                    && !profile.getLikedAuthors().isEmpty()) {
                context.append("Liked authors: ")
                        .append(String.join(", ", profile.getLikedAuthors()))
                        .append("\n");
            }

            if (profile.getDislikedSubjects() != null
                    && !profile.getDislikedSubjects().isEmpty()) {
                context.append("Disliked subjects: ")
                        .append(String.join(", ", profile.getDislikedSubjects()))
                        .append("\n");
            }

            if (profile.getReadingHistoryRecordIds() != null
                    && !profile.getReadingHistoryRecordIds().isEmpty()) {
                context.append("Previously read book record IDs: ")
                        .append(String.join(", ", profile.getReadingHistoryRecordIds()))
                        .append("\n");
            }

            if (profile.getRejectedRecordIds() != null
                    && !profile.getRejectedRecordIds().isEmpty()) {
                context.append("Previously rejected book record IDs: ")
                        .append(String.join(", ", profile.getRejectedRecordIds()))
                        .append("\n");
            }

            context.append("\n");
        });

        if (!likedBooks.isEmpty()) {
            context.append("Books the user liked:\n");

            for (Book book : likedBooks) {
                context.append("- Title: ")
                        .append(book.getTitle())
                        .append("\n  Description: ")
                        .append(book.getDescription())
                        .append("\n");
            }
        }

        if (!rejectedBooks.isEmpty()) {
            context.append("\nBooks the user rejected:\n");

            for (Book book : rejectedBooks) {
                context.append("- Title: ")
                        .append(book.getTitle())
                        .append("\n  Description: ")
                        .append(book.getDescription())
                        .append("\n");
            }
        }

        if (context.isEmpty()) {
            return "No previous user preference history is available.";
        }

        return context.toString();
    }
}