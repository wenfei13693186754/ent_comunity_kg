package com.kg.platform.util;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import jxl.Sheet;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommonUtils {

    public static double similaritySmooth(double vecSim, double factor1, double setSim, double factor2) {
        return (vecSim * setSim * factor1) + vecSim - factor2;
    }

    public static double getSetSimilarity(String str1, String str2) {

        String minStr = str1;
        String maxStr = str2;
        //比较两个字符串长度大小
        if (str1.length() > str2.length()) {
            minStr = str2;
            maxStr = str1;
        }
        //取两个字符串交集
        String intersection = strIntersection(minStr, maxStr);
        int maxStrLength = maxStr.length();
        int minStrLength = minStr.length();
        float ledScore = ((float) minStrLength / maxStrLength) * ((float) intersection.length() / minStrLength);
        return ledScore;
    }

    public static String strIntersection(String str1, String str2) {
        if (str1.length() > str2.length()) {
            String temp = str1;
            str1 = str2;
            str2 = temp;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str1.length(); i++) {
            String c = String.valueOf(str1.charAt(i));
            if (str2.contains(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static float levenshtein(float insertDelete, float substitute, final String str1, String str2) {
        if (str1.isEmpty())
            return str2.length();
        if (str2.isEmpty())
            return str1.length();
        if (str1.equals(str2))
            return 0;

        final int tLength = str2.length();
        final int sLength = str1.length();

        float[] swap;
        float[] v0 = new float[tLength + 1];
        float[] v1 = new float[tLength + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i * insertDelete;
        }

        for (int i = 0; i < sLength; i++) {

            // first element of v1 is A[i+1][0]
            // edit distance is delete (i+1) chars from s to match empty t
            v1[0] = (i + 1) * insertDelete;

            for (int j = 0; j < tLength; j++) {
                v1[j + 1] = Math.min(Math.min(v1[j] + insertDelete,
                        v0[j + 1] + insertDelete),
                        v0[j]
                                + (str1.charAt(i) == str2.charAt(j) ? 0.0f
                                : substitute));
            }

            swap = v0;
            v0 = v1;
            v1 = swap;
        }

        // latest results was in v1 which was swapped with v0
        return v0[tLength];
    }

    public static String formatProperty(Map<String, Object> properties) {
        if (properties.size() == 0) {
            return "{}";
        } else {
            StringBuilder sbd = new StringBuilder("{");
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                sbd.append(entry.getKey() + ":'" + entry.getValue() + "',");
            }
            sbd.deleteCharAt(sbd.lastIndexOf(",")).append("}");
            return sbd.toString();
        }
    }

    /**
     * 将字符串中的汉字转化为英文首字母，且是小写
     *
     * @param input
     * @return
     */
    public static String hanziZhuanHuan(String input) {
        String reg = "[\\u4e00-\\u9fa5]";
        String replacedStr = input.replaceAll(reg, "&");
        while (replacedStr.contains("&")) {
            int index = replacedStr.indexOf("&");
            String hanzi = input.substring(index, index + 1);
            String headerCase = getPinyinHeaderString(hanzi);
            input = input.replaceFirst(hanzi, headerCase);
            replacedStr = replacedStr.replaceFirst("&", "#");
        }
        return input;
    }

    /**
     * 提取每个汉字的首字母
     */
    public static String getPinyinHeaderString(String str) {
        //定义一个空字符串去接收
        String convert = "";
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            //提取汉字的首字目
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toLowerCase();
    }

    public static boolean hanziCunZai(String input) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = p.matcher(input);
        return matcher.find();
    }
}
