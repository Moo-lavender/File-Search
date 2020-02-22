package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 解析文件大小
     * @param size
     * @return
     */
    public static String parseSize(long size) {
        String[] danweis = {"B","KB","MB","GB"};
        int idx = 0;
        while(size > 1024 && idx < danweis.length - 1) {
            size /= 1024;
            idx ++;
        }
        return size + danweis[idx];
    }

    /**
     * 解析日期
     * @param lastModified
     * @return
     */
    public static String parseDate(Date lastModified) {
        return new SimpleDateFormat(DATE_PATTERN).format(lastModified);
    }

    public static void main(String[] args) {
        System.out.println(parseSize(1000));
    }
}
