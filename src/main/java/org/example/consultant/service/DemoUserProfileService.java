package org.example.consultant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.consultant.model.DemoUserProfile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class DemoUserProfileService {

    private final List<DemoUserProfile> profiles;

    public DemoUserProfileService() {
        this(new ObjectMapper());
    }

    DemoUserProfileService(ObjectMapper objectMapper) {
        // Load fixed demo users required for the prototype instead of a full user database.
        try (InputStream inputStream =
                     new ClassPathResource("demo-users.json").getInputStream()) {
            this.profiles = List.copyOf(
                    objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<DemoUserProfile>>() {
                            }
                    )
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load demo user profiles", e);
        }
    }

    public List<DemoUserProfile> getAllProfiles() {
        return profiles;
    }

    public Optional<DemoUserProfile> getProfileById(String id) {
        return profiles.stream()
                .filter(profile -> profile.getId().equals(id))
                .findFirst();
    }
}
