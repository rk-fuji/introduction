package com.example.introduction2.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SearchRequest implements Serializable {
    private Integer gender;
    private Integer prefecture;
    private String useLanguage;
    private String other;
    private Integer searchPattern;
}
