package com.amit.MoneyManager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.amit.MoneyManager.dto.CategoryDTO;
import com.amit.MoneyManager.entity.CategoryEntity;
import com.amit.MoneyManager.entity.ProfileEntity;
import com.amit.MoneyManager.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final ProfileService profileService;

  public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
    ProfileEntity profile = profileService.getCurrentProfile();
    if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
      throw new RuntimeException( "Category with this name already Exists");
        }
    CategoryEntity newCategory = toEntity(categoryDTO, profile);
    newCategory = categoryRepository.save(newCategory);
    return toDTO(newCategory);
  }

  public List<CategoryDTO> getCategoriesForCurrentUser(){
    ProfileEntity profile = profileService.getCurrentProfile();
    List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
    return categories.stream().map(this::toDTO).toList();
  }

  private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
    return CategoryEntity.builder()
       .name(categoryDTO.getName())
       .icon(categoryDTO.getIcon())
       .profile(profile)
       .type(categoryDTO.getType())
       .build();
}

  private CategoryDTO toDTO(CategoryEntity categoryEntity) {
    return CategoryDTO.builder()
       .id(categoryEntity.getId())
       .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
       .name(categoryEntity.getName())
        .icon(categoryEntity.getIcon())
        .type(categoryEntity.getType())
        .createdAt(categoryEntity.getCreatedAt())
        .updatedAt(categoryEntity.getUpdatedAt())
        .build();
  }
}