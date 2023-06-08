package com.example.introduction2.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtil {

    /**
     * 指定の画像ファイルを base64 へ変換した文字列で返却する
     * 
     * @param path
     * @param fileName
     * @return
     */
    public static String convertPictureToBase64(String path, String fileName) {
        var fullPath = Paths.get(path + fileName);
        if (!new File(path + fileName).exists()) {
            return "";
        }
        try {
            byte[] byteImg = Files.readAllBytes(fullPath);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(byteImg);
        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }
}
