package com.amit.MoneyManager.service;


import com.amit.MoneyManager.dto.AuthDTO;
import com.amit.MoneyManager.dto.ProfileDTO;
import com.amit.MoneyManager.entity.ProfileEntity;
import com.amit.MoneyManager.repository.ProfileRepository;
import com.amit.MoneyManager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class ProfileService {

    private final ProfileRepository profileRepository ;
    private final EmailServices emailServices;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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
                .password(passwordEncoder.encode(profileDTO.getPassword()))
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

    public boolean isEmailActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public boolean isAccountActive(String email) {
        return isEmailActive(email);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
       if (email == null || email.isEmpty()) {
              currentUser = getCurrentProfile();
       } else {
           currentUser = profileRepository.findByEmail(email)
                   .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
       }
        
       return ProfileDTO.builder()
               .id(currentUser.getId())
               .name(currentUser.getName())
               .email(currentUser.getEmail())
               .profileImageUrl(currentUser.getProfileImageUrl())
               .createdAt(currentUser.getCreatedAt())
               .updatedAt(currentUser.getUpdatedAt())
               .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        // Implementation for authenticating user and generating token
       try {
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
                   String token = jwtUtil.generateToken(authDTO.getEmail());
                   return Map.of("token", token,
                   "User", getPublicProfile(authDTO.getEmail())
           );

        } catch (Exception e) {
           throw new RuntimeException("Invalid credentials");
        }
    }
}
