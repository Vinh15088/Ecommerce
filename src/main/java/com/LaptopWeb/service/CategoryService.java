package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.CategoryRequest;
import com.LaptopWeb.dto.request.CategoryUpdateRequest;
import com.LaptopWeb.entity.Category;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.mapper.CategoryMapper;
import com.LaptopWeb.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);

        if(categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorApp.CATEGORY_NAME_EXISTED);
        }

        if(request.getParent_id() != null) {
            Category categoryParent = getCategoryById(request.getParent_id());

            category.setParent(categoryParent);
        }

        return categoryRepository.save(category);
    }

    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new AppException(ErrorApp.CATEGORY_NOT_FOUND));
    }

    public Category getByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() ->
                new AppException(ErrorApp.CATEGORY_NOT_FOUND));
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategory() {
        List<Category> categories = getAll();

        List<Category> results = new ArrayList<>();

        for(Category category:categories) {
            if(category.getParent() == null) results.add(category);
        }

        return results;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Category> getAllParentCategory() {
        return categoryRepository.findAllParentCategory();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Category> getPageCategory(Integer pageNum, Integer size, String sortField, String keyWord) {
        Sort sort = sortField != null ? Sort.by(sortField).ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum, size, sort);

        if(keyWord == null) {
            return categoryRepository.findAll(pageable);
        } else {
            return categoryRepository.findAllCategory(keyWord, pageable);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category updateCategory(Integer categoryId, CategoryUpdateRequest request) {
        Category category = getCategoryById(categoryId);

//        if(request.getName().equals(category.getName()) || categoryRepository.existsByName(request.getName())) {
//            throw new AppException(ErrorApp.CATEGORY_NAME_EXISTED);
//        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setEnabled(request.isEnabled());


        if(request.getParent_id() != null) {
            Category categoryParent = getCategoryById(request.getParent_id());

            category.setParent(categoryParent);
        } else {
            category.setParent(null);
        }

        return  categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Integer categoryId) {
        Category category = getCategoryById(categoryId);

        categoryRepository.deleteById(categoryId);
    }
}
