package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.parent IS null")
    List<Category> findAllParentCategory();

    @Query("SELECT c FROM Category c WHERE c.parent IS null AND c.name LIKE %?1%")
    Page<Category> findAllCategory(String keyword, Pageable pageable);
}
