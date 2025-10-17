package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 读取三列xlsx（中文名、区划编号adcode、城市编号citycode），
 * 识别省/市/区层级并导出可供三级联动使用的JSON。
 * 使用方式：java org.example.ExcelToAddressJson input.xlsx output.json
 */
public class ExcelToAddressJson {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("用法: java org.example.ExcelToAddressJson 输入xlsx 输出json");
            return;
        }
        Path in = Paths.get(args[0]);
        Path out = Paths.get(args[1]);
        Map<String,String> pName = new LinkedHashMap<>();              // 省code->省名
        Map<String,LinkedHashMap<String,String>> cMap = new LinkedHashMap<>(); // 省code->(市code->市名)
        Map<String,LinkedHashMap<String,String>> dMap = new LinkedHashMap<>(); // 市code->(区code->区名)
        DataFormatter fmt = new DataFormatter();
        try (Workbook wb = new XSSFWorkbook(Files.newInputStream(in))) {
            Sheet sh = wb.getSheetAt(0);
            for (Row r : sh) {
                if (r.getRowNum() == 0) continue; // 跳过表头
                String name = fmt.formatCellValue(r.getCell(0)).trim();
                String code = fmt.formatCellValue(r.getCell(1)).trim();
                if (name.isEmpty() || code.length()!=6 || !code.matches("\\d{6}")) continue;
                if ("100000".equals(code)) continue; // 国家级，忽略
                String prov = code.substring(0,2)+"0000";
                String city = code.substring(0,4)+"00";
                if (code.endsWith("0000")) { // 省
                    pName.put(prov, name);
                    cMap.putIfAbsent(prov, new LinkedHashMap<>());
                } else if (code.endsWith("00")) { // 市
                    pName.putIfAbsent(prov, prov);
                    cMap.computeIfAbsent(prov,k->new LinkedHashMap<>()).put(city, name);
                    dMap.putIfAbsent(city, new LinkedHashMap<>());
                } else { // 区
                    pName.putIfAbsent(prov, prov);
                    cMap.computeIfAbsent(prov,k->new LinkedHashMap<>()).putIfAbsent(city, city);
                    dMap.computeIfAbsent(city,k->new LinkedHashMap<>()).put(code, name);
                }
            }
        }
        String json = buildJson(pName, cMap, dMap);
        Files.write(out, json.getBytes(StandardCharsets.UTF_8));
        System.out.println("完成: " + out.toAbsolutePath());
    }

    private static String buildJson(Map<String,String> pName,
                                    Map<String,LinkedHashMap<String,String>> cMap,
                                    Map<String,LinkedHashMap<String,String>> dMap) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("{\"provinces\":[");
        boolean fp = true;
        for (Map.Entry<String,String> pe : pName.entrySet()) {
            if (!fp) sb.append(','); fp=false;
            String pCode = pe.getKey(); String pN = pe.getValue();
            sb.append("{\"name\":\"").append(esc(pN)).append("\",\"code\":\"").append(pCode).append("\",\"cities\":[");
            boolean fc = true;
            LinkedHashMap<String,String> cities = cMap.get(pCode);
            if (cities!=null) {
                for (Map.Entry<String,String> ce : cities.entrySet()) {
                    if (!fc) sb.append(','); fc=false;
                    String cCode = ce.getKey(); String cN = ce.getValue();
                    sb.append("{\"name\":\"").append(esc(cN)).append("\",\"code\":\"").append(cCode).append("\",\"districts\":[");
                    boolean fd = true;
                    LinkedHashMap<String,String> dists = dMap.get(cCode);
                    if (dists!=null) {
                        for (Map.Entry<String,String> de : dists.entrySet()) {
                            if (!fd) sb.append(','); fd=false;
                            sb.append("{\"name\":\"").append(esc(de.getValue())).append("\",\"code\":\"").append(de.getKey()).append("\"}");
                        }
                    }
                    sb.append("]}");
                }
            }
            sb.append("]}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String esc(String s){
        return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"");
    }
}
