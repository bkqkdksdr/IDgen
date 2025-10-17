package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;

/**
 * 地址码工具：仅从类路径加载 resources/output.json，并使用 Jackson 解析。
 * 提供省/市/区三级联动查询方法，供界面组件调用。
 */
public class AddressCodeUtil {
    public static final Map<String, String> PROVINCES = new LinkedHashMap<>();            // 省code -> 省名
    public static final Map<String, Map<String, String>> CITIES = new LinkedHashMap<>();  // 省code -> (市code -> 市名)
    public static final Map<String, Map<String, String>> DISTRICTS = new LinkedHashMap<>(); // 市code -> (区code -> 区名)

    static { load(); }

    /** 仅从类路径读取 resources/output.json 并解析 */
    public static void load() {
        PROVINCES.clear();
        CITIES.clear();
        DISTRICTS.clear();
        try (InputStream is = AddressCodeUtil.class.getClassLoader().getResourceAsStream("output.json")) {
            if (is == null) {
                // 类路径未找到时保持空映射，交由上层提示并退出
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            Root root = mapper.readValue(is, Root.class);
            if (root == null || root.provinces == null) return;
            for (Province p : root.provinces) {
                if (p == null || p.code == null) continue;
                String pCode = p.code;
                String pName = p.name == null ? pCode : p.name;
                PROVINCES.put(pCode, pName);

                Map<String, String> cityMap = new LinkedHashMap<>();
                if (p.cities != null) {
                    for (City c : p.cities) {
                        if (c == null || c.code == null) continue;
                        String cCode = c.code;
                        String cName = c.name == null ? cCode : c.name;
                        cityMap.put(cCode, cName);

                        Map<String, String> distMap = new LinkedHashMap<>();
                        if (c.districts != null) {
                            for (District d : c.districts) {
                                if (d == null || d.code == null) continue;
                                distMap.put(d.code, d.name == null ? d.code : d.name);
                            }
                        }
                        DISTRICTS.put(cCode, distMap);
                    }
                }
                CITIES.put(pCode, cityMap);
            }
        } catch (Exception e) {
            // 解析失败保持空映射
        }
    }

    // --- 对外方法：供界面使用 ---
    public static List<String> getProvinceNames() {
        return new ArrayList<>(PROVINCES.values());
    }

    public static String getProvinceCode(String provinceName) {
        for (Map.Entry<String, String> e : PROVINCES.entrySet()) {
            if (e.getValue().equals(provinceName)) return e.getKey();
        }
        return null;
    }

    public static List<String> getCityNames(String provinceCode) {
        Map<String, String> m = CITIES.get(provinceCode);
        return m == null ? Collections.emptyList() : new ArrayList<>(m.values());
    }

    public static String getCityCode(String provinceCode, String cityName) {
        Map<String, String> m = CITIES.get(provinceCode);
        if (m != null) {
            for (Map.Entry<String, String> e : m.entrySet()) {
                if (e.getValue().equals(cityName)) return e.getKey();
            }
        }
        return null;
    }

    public static List<String> getDistrictNames(String cityCode) {
        Map<String, String> m = DISTRICTS.get(cityCode);
        return m == null ? Collections.emptyList() : new ArrayList<>(m.values());
    }

    public static String getDistrictCode(String cityCode, String districtName) {
        Map<String, String> m = DISTRICTS.get(cityCode);
        if (m != null) {
            for (Map.Entry<String, String> e : m.entrySet()) {
                if (e.getValue().equals(districtName)) return e.getKey();
            }
        }
        return null;
    }

    /** 获取完整地址码（优先区县，其次城市的前6位，最后省） */
    public static String getFullAddressCode(String provinceName, String cityName, String districtName) {
        String p = getProvinceCode(provinceName);
        if (p == null) return null;
        String c = getCityCode(p, cityName);
        if (c == null) return p;
        String d = getDistrictCode(c, districtName);
        return d != null ? d : (c.length() >= 6 ? c.substring(0, 6) : c);
    }

    // --- Jackson 模型类 ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Root { public List<Province> provinces; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Province { public String name; public String code; public List<City> cities; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class City { public String name; public String code; public List<District> districts; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class District { public String name; public String code; }
}