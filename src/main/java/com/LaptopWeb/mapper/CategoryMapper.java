package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.CategoryRequest;
import com.LaptopWeb.dto.response.CategoryResponse;
import com.LaptopWeb.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

//    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryRequest request);

    @Mapping(target = "parent_id", expression = "java(buildParentId(category))")
    @Mapping(target = "children", expression = "java(buildChildren(category))")
    CategoryResponse toCategoryResponse(Category category);

    default List<CategoryResponse> buildChildren(Category category) {
        return category.getChildren() != null
                ? category.getChildren().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList())
                : null;
    }

    default Integer buildParentId(Category category) {
        return category.getParent() != null
                ? category.getParent().getId()
                : null;
    }
}
