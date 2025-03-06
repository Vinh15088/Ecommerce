package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.BrandRequest;
import com.LaptopWeb.entity.Brand;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final AwsS3Service awsS3Service;

    @PreAuthorize("hasRole('ADMIN')")
    public Brand createBrand(BrandRequest request, MultipartFile logo) throws Exception {
        if(brandRepository.existsByName(request.getName())) throw new AppException(ErrorApp.BRAND_NAME_EXISTED);

        Brand brand = Brand.builder()
                .name(request.getName())
                .logo(logo.getOriginalFilename())
                .build();

        Brand brand1 = brandRepository.save(brand);

        String folder = "brands/" + brand1.getId();

        awsS3Service.saveImageToS3(logo, folder);

        return brand1;
    }

    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    public Brand getById(Integer brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() ->
                new AppException(ErrorApp.BRAND_NOT_FOUND));

        return brand;
    }

    public Brand getByName(String brandName) {
        Brand brand = brandRepository.findByName(brandName).orElseThrow(() ->
                new AppException(ErrorApp.BRAND_NOT_FOUND));

        return brand;
    }

    public List<Brand> getByKeyOfName(String name) {
        List<Brand> brands = (List<Brand>) brandRepository.findListByName(name).orElseThrow(() ->
                new AppException(ErrorApp.BRAND_NOT_FOUND));

        return brands;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Brand> getPageBrand(Integer pageNum, Integer size, String sortField, String keyWord) {
        Sort sort = sortField != null ? Sort.by(sortField).ascending() : Sort.unsorted();

        Pageable pageable = PageRequest.of(pageNum, size, sort);

        if(keyWord == null) return brandRepository.findAll(pageable);
        else return brandRepository.findAllBrand(keyWord, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Brand updateBrand(Integer brandId, BrandRequest request, MultipartFile logo) throws Exception {
        Brand brand = getById(brandId);

        brand.setName(request.getName());

        String folder = "brands/" + brandId;

        if(logo != null) {
            awsS3Service.deleteImageFromS3(folder, brand.getLogo());

            brand.setLogo(logo.getOriginalFilename());

            awsS3Service.saveImageToS3(logo, folder);
        }

        return brandRepository.save(brand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBrand(Integer brandId) throws Exception {
        Brand brand = getById(brandId);

        String folder = "brands/" + brandId;

        awsS3Service.deleteImageFromS3(folder, brand.getLogo());

        brandRepository.deleteById(brandId);
    }


}
