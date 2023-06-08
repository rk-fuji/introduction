package com.example.introduction2.util;

import java.time.LocalDate;

public class DateUtil {
    /**
     * YYYY-MM 形式の日付文字列を LocalDate へ変換して返却する
     * 日付(DD)は必要なため、YYYY-MM-01 と加工する
     * 
     * null もしくは "" の場合は null を返却する
     * 
     * @param date 日付文字列(YYYY-MM)
     * @return
     */
    public static LocalDate StringDateToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(String.format("%s-01", date));
    }
}
