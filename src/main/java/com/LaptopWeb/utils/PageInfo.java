package com.LaptopWeb.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageInfo {
    public static final String PAGE_NUMBER = "1";
    public static final String PAGE_SIZE = "5";

    Integer size;
    Integer totalElements;
    Integer totalPages;
    Integer page;
    boolean hasNext;
    boolean hasPrevious;
}
