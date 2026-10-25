package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.DemoUserProfile;
import org.example.consultant.model.UserFeedback;
import org.example.consultant.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

    @Mock
    private UserFeedbackService feedbackService;

    @Mock
    private BookRepository bookRepository;

    @Spy
    private DemoUserProfileService demoUserProfileService = new DemoUserProfileService();

    @InjectMocks
    private UserPreferenceService preferenceService;

    @ParameterizedTest(name = "Preference context for {0}")
    @ValueSource(strings = {"romance-reader", "science-reader", "self-improvement-reader",
            "mystery-reader", "history-adventure-reader", "new-gentle-fiction-reader"})
    void shouldBuildPreferenceContextFromActualDemoProfileAndFeedback(String userId) {
        // Load the production JSON through the real profile service.
        DemoUserProfile profile = demoUserProfileService.getProfileById(userId).orElseThrow();
        Book likedBook = book("live-liked", "Live Liked Book", "A liked book.");
        Book rejectedBook = book("live-rejected", "Live Rejected Book", "A rejected book.");
        when(feedbackService.getFeedbackForUser(userId)).thenReturn(List.of(
                new UserFeedback(userId, "live-liked", "LIKED"),
                new UserFeedback(userId, "live-rejected", "REJECTED")
        ));
        when(bookRepository.findById("live-liked")).thenReturn(Optional.of(likedBook));
        when(bookRepository.findById("live-rejected")).thenReturn(Optional.of(rejectedBook));
        // The JSON supplies real IDs, not catalogue metadata. Only catalogue lookups are mocked.
        for (String savedId : profile.getSavedBookRecordIds()) {
            when(bookRepository.findById(savedId)).thenReturn(Optional.of(
                    book(savedId, "Catalogue fixture " + savedId, "Saved book description")));
        }

        String context = preferenceService.buildPreferenceContext(userId);

        assertTrue(context.contains("Stated preferences: " + profile.getStatedPreferences()));
        assertTrue(context.contains("Disliked subjects: " + String.join(", ", profile.getDislikedSubjects())));
        if (!profile.getFavoriteBookTitles().isEmpty()) {
            assertTrue(context.contains("Favorite books: " + String.join(", ", profile.getFavoriteBookTitles())));
        }
        if (!profile.getReadingHistoryRecordIds().isEmpty()) {
            assertTrue(context.contains("Previously read book record IDs: "
                    + String.join(", ", profile.getReadingHistoryRecordIds())));
        } else {
            assertFalse(context.contains("Previously read book record IDs:"));
        }
        if (!profile.getRejectedRecordIds().isEmpty()) {
            assertTrue(context.contains("Previously rejected book record IDs: "
                    + String.join(", ", profile.getRejectedRecordIds())));
        }
        for (String savedId : profile.getSavedBookRecordIds()) {
            assertTrue(context.contains("Catalogue fixture " + savedId));
        }
        assertTrue(context.contains("Live Liked Book"));
        assertTrue(context.contains("Live Rejected Book"));
        // No other demo user's stated preferences should leak into this context.
        demoUserProfileService.getAllProfiles().stream()
                .filter(other -> !other.getId().equals(userId))
                .forEach(other -> assertFalse(
                        context.contains(other.getStatedPreferences())));
    }

    @Test
    void shouldReturnDefaultMessageWhenUserHasNoPreferenceHistory() {
        String userId = "new-user";

        when(feedbackService.getFeedbackForUser(userId)).thenReturn(List.of());

        String context = preferenceService.buildPreferenceContext(userId);

        assertEquals("No previous user preference history is available.", context);
    }

    private Book book(String recordId, String title, String description) {
        return new Book(
                recordId,
                title,
                List.of("Example Author"),
                description,
                "VA",
                title.toLowerCase(),
                List.of("example author"),
                true
        );
    }
}
