package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 身份证号生成工具：18 位号码按 GB 11643 校验码规则。
 * 结构：地址码(6) + 生日(yyyyMMdd) + 顺序码(3，奇数男/偶数女) + 校验码(1)
 */
public class IdNumberUtil {

    /**
     * 生成 18 位身份证号。
     * @param addressCode 6 位地址码
     * @param birth yyyyMMdd 格式生日
     * @param male 是否男性（奇数）
     * @param seqInput 可选顺序码（1-999），为 null/空则自动生成并按性别修正奇偶
     */
    public static String generateIdNumber(String addressCode, String birth, boolean male, String seqInput) {
        String seq = seqInput;
        if (seq == null || seq.isEmpty()) {
            int r = new Random().nextInt(1000); // 0-999
            r = male ? (r | 1) : (r & ~1);       // 奇数男、偶数女
            seq = String.format("%03d", r);
        } else {
            try {
                int v = Integer.parseInt(seq);
                if (v < 1 || v > 999) throw new NumberFormatException();
                boolean isOdd = (v % 2) == 1;
                if (male && !isOdd) v += 1; // 调整为奇数
                if (!male && isOdd) v += 1; // 调整为偶数
                if (v > 999) v -= 2; // 边界修正
                seq = String.format("%03d", v);
            } catch (NumberFormatException ex) {
                seq = male ? "001" : "002";
            }
        }

        String base = addressCode + birth + seq;
        char check = calculateCheckCode(base);
        return base + check;
    }

    /** GB 11643 校验码计算 */
    public static char calculateCheckCode(String base17) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] codes = {'1','0','X','9','8','7','6','5','4','3','2'};
        int sum = 0;
        for (int i = 0; i < base17.length(); i++) {
            sum += (base17.charAt(i) - '0') * weights[i];
        }
        return codes[sum % 11];
    }

    /** 将 LocalDate 转 yyyyMMdd 字符串（辅助） */
    public static String formatBirth(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}