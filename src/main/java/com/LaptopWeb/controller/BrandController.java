package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.BrandRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.BrandResponse;
import com.LaptopWeb.entity.Brand;
import com.LaptopWeb.mapper.BrandMapper;
import com.LaptopWeb.service.BrandService;
import com.LaptopWeb.utils.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private static final String PAGE_NUMBER = "1";
    private static final String PAGE_SIZE = "10";

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandMapper brandMapper;

    @PostMapping /*checked success*/
    public ResponseEntity<?> createBrand(
            @Valid
            @RequestPart("brand") BrandRequest request,
            @RequestPart("logo") MultipartFile logo
    ) throws Exception {

        Brand brand = brandService.createBrand(request, logo);

        BrandResponse brandResponse = brandMapper.toBrandResponse(brand);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{brandId}") /*checked success*/
    public ResponseEntity<ApiResponse<?>> getBrandById(@PathVariable("brandId") Integer brandId) {
        Brand brand = brandService.getById(brandId);

        BrandResponse brandResponse = brandMapper.toBrandResponse(brand);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/name") /*checked success*/
    public ResponseEntity<ApiResponse<?>> getBrandById(@RequestParam("brandName") String brandName) {
        Brand brand = brandService.getByName(brandName);

        BrandResponse brandResponse = brandMapper.toBrandResponse(brand);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

//    @GetMapping /*checked success*/
//    public ResponseEntity<ApiResponse<?>> getBrandByKeyOfName(@RequestParam(name = "name", required = false) String name) {
//        if(name != null && !name.isEmpty()) {
//            List<Brand> brands = brandService.getByKeyOfName(name);
//
//            List<BrandResponse> listBrandResponse = brands.stream().map(brandMapper::toBrandResponse).toList();
//
//            ApiResponse<?> apiResponse = ApiResponse.builder()
//                    .success(true)
//                    .content(listBrandResponse)
//                    .build();
//
//            return ResponseEntity.ok().body(apiResponse);
//        } else {
//            List<Brand> brands = brandService.getAllBrand();
//
//            List<BrandResponse> listBrandResponse = brands.stream().map(brandMapper::toBrandResponse).toList();
//
//            ApiResponse<?> apiResponse = ApiResponse.builder()
//                    .success(true)
//                    .content(listBrandResponse)
//                    .build();
//
//            return ResponseEntity.ok().body(apiResponse);
//        }
//    }

    @GetMapping("/all") /*checked success*/
    public ResponseEntity<ApiResponse<?>> getAllBrand() {
        List<Brand> brands = brandService.getAllBrand();

        List<BrandResponse> listBrandResponse = brands.stream().map(brandMapper::toBrandResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(listBrandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getBrandPage(
            @RequestParam(name = "pageNumber", defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortField") String sortField,
            @RequestParam(name = "keyWord") String keyWord
    ) {
        Page<Brand> page = brandService.getPageBrand(pageNumber-1, pageSize, sortField, keyWord);

        List<Brand> brands = page.getContent();
        List<BrandResponse> listBrandResponse = brands.stream().map(brandMapper::toBrandResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getNumberOfElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(listBrandResponse)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @PutMapping("/{brandId}") /*checked success*/
    public ResponseEntity<ApiResponse<?>> updateBrand(
            @Valid
            @PathVariable("brandId") Integer brandId,
            @RequestPart(name = "brand", required = false) BrandRequest request,
            @RequestPart(name = "logo", required = false) MultipartFile logo) throws Exception {

        Brand brand = brandService.updateBrand(brandId, request, logo);

        BrandResponse brandResponse = brandMapper.toBrandResponse(brand);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{brandId}") /*checked success*/
    public ResponseEntity<ApiResponse<?>> deleteBrand (@PathVariable("brandId") Integer brandId) throws Exception {
        brandService.deleteBrand(brandId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Delete Brand Successfully")
                .build();

        return ResponseEntity.ok().body(apiResponse);

    }
}
