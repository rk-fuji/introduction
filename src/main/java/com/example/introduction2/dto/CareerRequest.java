package com.example.introduction2.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CareerRequest implements Serializable {
    // 期間(From, To)
    private String fromDate;
    private String toDate;
    // 業務内容(タイトル)
    private String businessOutline;
    // 業務内容(本文)
    private String businessOutlineDescription;
    // 役割(タイトル)
    private String role;
    // 役割(本文)
    private String roleDescription;
    // 使用言語
    private String useLanguage;
    // DB
    private String useDatabase;
    // サーバ
    private String useServer;
    // FW・MWツール等
    private String other;
    // 担当工程
    private String responsibleProcess;
}
