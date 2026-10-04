package org.example.consultant.service;

import org.example.consultant.model.Book;
import org.example.consultant.model.DemoUserProfile;
import org.example.consultant.model.UserFeedback;
import org.example.consultant.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

    @Mock
    private UserFeedbackService feedbackService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private DemoUserProfileService demoUserProfileService;

    @InjectMocks
    private UserPreferenceService preferenceService;

    @Test
    void shouldBuildPreferenceContextFromDemoProfileAndFeedback() {
        String userId = "science-reader";
        DemoUserProfile profile = new DemoUserProfile();
        profile.setStatedPreferences("I like space and technology.");
        profile.setFavoriteBookTitles(List.of("The Little Book of Exoplanets"));
        profile.setLikedAuthors(List.of("Lucy Hawking"));
        profile.setDislikedSubjects(List.of("romance"));
        profile.setReadingHistoryRecordIds(List.of("read-1"));
        profile.setRejectedRecordIds(List.of("profile-rejected-1"));
        profile.setSavedBookRecordIds(List.of("saved-1"));
        Book savedBook = book("saved-1", "Saved Space Book", "A saved science book.");
        Book likedBook = book("liked-1", "Liked Science Book", "A liked science book.");
        Book rejectedBook = book("rejected-1", "Rejected Romance Book", "A rejected romance book.");

        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.of(profile));
        when(feedbackService.getFeedbackForUser(userId)).thenReturn(List.of(
                new UserFeedback(userId, "liked-1", "LIKED"),
                new UserFeedback(userId, "rejected-1", "REJECTED")
        ));
        when(bookRepository.findById("saved-1")).thenReturn(Optional.of(savedBook));
        when(bookRepository.findById("liked-1")).thenReturn(Optional.of(likedBook));
        when(bookRepository.findById("rejected-1")).thenReturn(Optional.of(rejectedBook));

        String context = preferenceService.buildPreferenceContext(userId);

        assertTrue(context.contains("I like space and technology."));
        assertTrue(context.contains("Favorite books: The Little Book of Exoplanets"));
        assertTrue(context.contains("Liked authors: Lucy Hawking"));
        assertTrue(context.contains("Disliked subjects: romance"));
        assertTrue(context.contains("Previously read book record IDs: read-1"));
        assertTrue(context.contains("Previously rejected book record IDs: profile-rejected-1"));
        assertTrue(context.contains("Saved Space Book"));
        assertTrue(context.contains("Liked Science Book"));
        assertTrue(context.contains("Rejected Romance Book"));
    }

    @Test
    void shouldReturnDefaultMessageWhenUserHasNoPreferenceHistory() {
        String userId = "new-user";

        when(demoUserProfileService.getProfileById(userId)).thenReturn(Optional.empty());
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
