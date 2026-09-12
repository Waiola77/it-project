package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.UserFeedback;
import org.example.consultant.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceService {

    private final UserFeedbackService feedbackService;
    private final BookRepository bookRepository;

    public UserPreferenceService(
            UserFeedbackService feedbackService,
            BookRepository bookRepository) {

        this.feedbackService = feedbackService;
        this.bookRepository = bookRepository;
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