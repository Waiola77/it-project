package org.example.consultant.model;

import java.util.List;

// Demo user profile used for the prototype recommendation scenarios.
public class DemoUserProfile {

    private String id;
    private String name;
    private String statedPreferences;
    private List<String> favoriteBookTitles;
    private List<String> likedAuthors;
    private List<String> dislikedSubjects;
    private List<String> readingHistoryRecordIds;
    private List<String> rejectedRecordIds;
    private List<String> savedBookRecordIds;

    public DemoUserProfile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatedPreferences() {
        return statedPreferences;
    }

    public void setStatedPreferences(String statedPreferences) {
        this.statedPreferences = statedPreferences;
    }

    public List<String> getFavoriteBookTitles() {
        return favoriteBookTitles;
    }

    public void setFavoriteBookTitles(List<String> favoriteBookTitles) {
        this.favoriteBookTitles = favoriteBookTitles;
    }

    public List<String> getLikedAuthors() {
        return likedAuthors;
    }

    public void setLikedAuthors(List<String> likedAuthors) {
        this.likedAuthors = likedAuthors;
    }

    public List<String> getDislikedSubjects() {
        return dislikedSubjects;
    }

    public void setDislikedSubjects(List<String> dislikedSubjects) {
        this.dislikedSubjects = dislikedSubjects;
    }

    public List<String> getReadingHistoryRecordIds() {
        return readingHistoryRecordIds;
    }

    public void setReadingHistoryRecordIds(List<String> readingHistoryRecordIds) {
        this.readingHistoryRecordIds = readingHistoryRecordIds;
    }

    public List<String> getRejectedRecordIds() {
        return rejectedRecordIds;
    }

    public void setRejectedRecordIds(List<String> rejectedRecordIds) {
        this.rejectedRecordIds = rejectedRecordIds;
    }

    public List<String> getSavedBookRecordIds() {
        return savedBookRecordIds;
    }

    public void setSavedBookRecordIds(List<String> savedBookRecordIds) {
        this.savedBookRecordIds = savedBookRecordIds;
    }
}
