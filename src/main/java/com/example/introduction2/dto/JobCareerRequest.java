package com.example.introduction2.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class JobCareerRequest implements Serializable {
    // フリガナ
    private String nameKana;
    // 所属
    private String affiliation;
    // 氏名
    private String name;
    // 性別
    private String gender;
    // 最寄駅
    private String nearestStation;
    // 年齢
    private Integer age;
    // 稼働
    private String operation;
    // 配偶者
    private String spouse;
    // 経歴(年)
    private String careerDate;
    // 経歴(月)
    private String careerMonth;
    // 得意分野
    private String specialty;
    // 得意技術
    private String favoriteTechnology;
    // 得意業務
    private String favoriteBusiness;
    // 自己PR
    private String selfPublicRelations;
}
