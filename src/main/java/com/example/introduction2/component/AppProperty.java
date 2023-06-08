package com.example.introduction2.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "app.introduction2")
public class AppProperty {
    private String defaultImagePath;
    private String defaultImageFileName;
    private String uploadImagePath;
    private String uploadImageTmpPath;
}
