package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自包含的测试：构造一个临时xlsx，调用ExcelToAddressJson生成JSON，并做基本断言。
 */
public class ExcelToAddressJsonTest {

    @Disabled("使用真实xlsx和输出路径，按需手动执行")
    @Test
    void convertRealXlsxToJson() throws Exception {
        Path input = Paths.get("D:\\ZYE\\JAVA\\IDgen\\src\\main\\resources\\AMap_adcode_citycode.xlsx");
        Path output = Paths.get("D:\\ZYE\\JAVA\\IDgen\\src\\main\\resources\\output.json");
        ExcelToAddressJson.main(new String[]{input.toString(), output.toString()});
    }

    private static void add(Sheet sh, int rowIndex, String name, String adcode, String citycode) {
        Row r = sh.createRow(rowIndex);
        r.createCell(0).setCellValue(name);
        r.createCell(1).setCellValue(adcode);
        r.createCell(2).setCellValue(citycode);
    }
}