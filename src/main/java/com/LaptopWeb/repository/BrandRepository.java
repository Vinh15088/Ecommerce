package com.LaptopWeb.repository;

import com.LaptopWeb.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    boolean existsByName(String name);

    Optional<Brand> findByName(String name);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) " +
            "LIKE LOWER(CONCAT('%', :name, '%'))")
    Optional<List<Brand>> findListByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAllBrand(String keyWord,Pageable pageable);
}
