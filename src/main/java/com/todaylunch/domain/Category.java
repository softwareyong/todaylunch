package com.todaylunch.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter
public enum Category {
    ALL("전체"),
    KOREAN("한식", "분식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식", "패스트푸드"),
    OTHERS("베트남음식", "동남아음식", "태국음식");

    private final List<String> labels;
    private static final Random RANDOM = new Random();

    Category(String... labels) {
        this.labels = Arrays.asList(labels);
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getRandomLabel() {
        return labels.get(RANDOM.nextInt(labels.size()));
    }
}
