package com.amit.MoneyManager.controller;

import com.amit.MoneyManager.dto.AuthDTO;
import com.amit.MoneyManager.dto.ProfileDTO;
import com.amit.MoneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam  String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
       try {
           if(!profileService.isAccountActive(authDTO.getEmail())) {
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Account is not activated. Please activate your account."));
           } else {
                 Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
                return ResponseEntity.ok(response);
           }
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
       }
    }

    @GetMapping("/test")
    public String test(){
        return "test successful";
    }
}

























