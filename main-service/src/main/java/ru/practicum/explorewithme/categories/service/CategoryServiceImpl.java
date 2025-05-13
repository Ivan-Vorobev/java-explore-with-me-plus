package ru.practicum.explorewithme.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.dto.NewCategoryDto;
import ru.practicum.explorewithme.categories.mapper.CategoryMapper;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.categories.repository.CategoryRepository;
import ru.practicum.explorewithme.exception.DataAlreadyExistException;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.toModel(newCategoryDto);
        try {
            Category createdCategory = categoryRepository.save(newCategory);
            return categoryMapper.toDto(createdCategory);
        } catch (DataIntegrityViolationException e) {
            final String error = String.format("The category with name=%s already exists in the database.",
                    newCategory.getName());
            log.warn(error);
            throw new DataAlreadyExistException(error);
        }
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        Category currentCategory = getCategoryById(categoryId);
        currentCategory.setName(newCategoryDto.getName());
        try {
            Category updatedCategory = categoryRepository.save(currentCategory);
            return categoryMapper.toDto(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            final String error = String.format("The category with name=%s already exists in the database.",
                    newCategoryDto.getName());
            log.warn(error);
            throw new DataAlreadyExistException(error);
        }

    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        Category currentCategory = getCategoryById(categoryId);
        return categoryMapper.toDto(currentCategory);
    }

    @Override
    public Collection<CategoryDto> getCategories(Long from, Integer size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").ascending());
        return categoryRepository.findByIdGreaterThanEqual(from, pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d not found.", categoryId)));
    }
}
