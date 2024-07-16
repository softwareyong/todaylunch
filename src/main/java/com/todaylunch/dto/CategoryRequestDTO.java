package com.todaylunch.dto;

import com.todaylunch.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CategoryRequestDTO {
    private List<Category> category;
}

