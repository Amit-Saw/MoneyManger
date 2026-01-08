package com.amit.MoneyManager.service;


import com.amit.MoneyManager.dto.ProfileDTO;
import com.amit.MoneyManager.entity.ProfileEntity;
import com.amit.MoneyManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor

public class ProfileService {

    private final ProfileRepository profileRepository ;
    private final EmailServices emailServices;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Implementation for registering a profile
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Send activation email logic
        try {
            // Context path is /api/v1.0 and controller mapping is /profile/activate
            String activationLink = "http://localhost:8080/api/v1.0/profile/activate?token=" + newProfile.getActivationToken();
            String subject = "Activate your account";
            String body = "Click the following link to activate your account: " + activationLink;
            if (emailServices != null) {
                emailServices.sendEmail(newProfile.getEmail(), subject, body);
            }
        } catch (Exception e) {
            System.err.println("Email sending failed, but profile was saved: " + e.getMessage());
            e.printStackTrace();
        }
        return toDTO(newProfile);

    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .name(profileDTO.getName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .name(profileEntity.getName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .isActive(profileEntity.getIsActive())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken).map(profile -> {
            profile.setIsActive(true);
            profile.setActivationToken(null); // Clear the token after activation
            profileRepository.save(profile);
            return true;
        }).orElse(false);
    }
}
