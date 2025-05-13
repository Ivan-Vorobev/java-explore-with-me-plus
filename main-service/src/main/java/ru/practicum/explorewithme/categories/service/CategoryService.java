package ru.practicum.explorewithme.categories.service;

import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto getCategory(Long categoryId);

    Collection<CategoryDto> getCategories(Long from, Integer size);
}
