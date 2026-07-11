package com.example.moneymanagement.service;

import com.example.moneymanagement.DTO.CategoryDTO;
import com.example.moneymanagement.entity.CategoryEntity;
import com.example.moneymanagement.entity.ProfileEntity;
import com.example.moneymanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
       ProfileEntity profile= profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfile_Id(categoryDTO.getName(),profile.getId())){
            throw new RuntimeException("Category with this name already exists");
        }
        CategoryEntity categoryEntity=toEntity(categoryDTO,profile);
        categoryRepository.save(categoryEntity);
        return toDTO(categoryEntity);
    }

    //get categories of current user
    public List<CategoryDTO> getCatagoriesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>catagories=categoryRepository.findByProfile_Id(profile.getId());
        return catagories.stream().map(this::toDTO).toList();
    }

    //get categories by type for current user
    public List<CategoryDTO>getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>entity=categoryRepository.findByTypeAndProfile_Id(type,profile.getId());
        return entity.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto){
        ProfileEntity profile=profileService.getCurrentProfile();
       CategoryEntity existingCategory= categoryRepository.findByIdAndProfile_Id(categoryId,profile.getId())
                .orElseThrow(()->new RuntimeException("Category not found or not accessible"));
       existingCategory.setName(dto.getName());
       existingCategory.setIcon(dto.getIcon());
       existingCategory=categoryRepository.save(existingCategory);
       return toDTO(existingCategory);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity){
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile()!=null ? entity.getProfile().getId():null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();

    }

}
