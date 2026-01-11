package com.amit.MoneyManager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amit.MoneyManager.dto.CategoryDTO;
import com.amit.MoneyManager.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/category")
public class CategoryController {
  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
    CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
  }

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getCategories() {
    List<CategoryDTO> categories = categoryService.getCategoriesForCurrentUser();
    return ResponseEntity.ok(categories);
  }
}
