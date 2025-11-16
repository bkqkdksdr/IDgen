package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Utils {
    // 姓氏数组
    private static final String[] FAMILY_NAMES = {
        "王", "李", "张", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗",
        "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧",
        "程", "曹", "袁", "邓", "许", "傅", "沈", "曾", "彭", "吕",
        "苏", "卢", "蒋", "蔡", "贾", "丁", "魏", "薛", "叶", "阎",
        "余", "潘", "杜", "戴", "夏", "钟", "汪", "田", "任", "姜",
        "范", "方", "石", "姚", "谭", "廖", "邹", "熊", "金", "陆",
        "郝", "孔", "白", "崔", "康", "毛", "邱", "秦", "江", "史",
        "顾", "侯", "邵", "孟", "龙", "万", "段", "漕", "钱", "汤",
        "尹", "黎", "易", "常", "武", "乔", "贺", "赖", "龚", "文"
    };
    
    // 名字数组（单字）
    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
        "勇", "艳", "杰", "涛", "明", "超", "霞", "平", "刚", "波",
        "德", "梅", "雪", "辉", "英", "健", "国", "兰", "慧", "永",
        "红", "祥", "凤", "琴", "华"
    };
    
    // 街道数组
    private static final String[] STREETS = {
        "中山路", "解放路", "人民路", "建设路", "公园路",
        "和平路", "中华路", "胜利路", "青年路", "延安路"
    };
    
    // 社区数组
    private static final String[] COMMUNITIES = {
        "阳光小区", "花园社区", "幸福家园", "和谐小区", "温馨家园",
        "绿色家园", "金色家园", "蓝色港湾", "梦幻社区"
    };
    
    private static final Random RANDOM = new Random();
    
    /**
     * 生成随机姓名
     * @return 随机生成的姓名
     */
    public static String generateRandomName() {
        String familyName = FAMILY_NAMES[RANDOM.nextInt(FAMILY_NAMES.length)];
        String givenName = GIVEN_NAMES[RANDOM.nextInt(GIVEN_NAMES.length)];
        return familyName + givenName;
    }
    
    /**
     * 生成详细地址
     * @param provinceName 省份名称
     * @param cityName 城市名称
     * @param districtName 区县名称
     * @return 生成的详细地址
     */
    public static String generateDetailedAddress(String provinceName, String cityName, String districtName) {
        String street = STREETS[RANDOM.nextInt(STREETS.length)];
        int streetNumber = RANDOM.nextInt(999) + 1;
        String community = COMMUNITIES[RANDOM.nextInt(COMMUNITIES.length)];
        int buildingNumber = RANDOM.nextInt(99) + 1;
        int unitNumber = RANDOM.nextInt(8) + 1;
        int roomNumber = RANDOM.nextInt(10) + 1;
        
        return provinceName + cityName + districtName + street + streetNumber + "号" + 
               community + buildingNumber + "栋" + unitNumber + "单元" + roomNumber + "室";
    }
    
    /**
     * 简化省份名称（去除自治区等后缀）
     * @param provinceName 原始省份名称
     * @return 简化后的省份名称
     */
    public static String simplifyProvinceName(String provinceName) {
        if (provinceName.contains("新疆")) {
            return "新疆";
        }
        if (provinceName.contains("内蒙古")) {
            return "内蒙古";
        }
        if (provinceName.contains("西藏")) {
            return "西藏";
        }
        if (provinceName.contains("宁夏")) {
            return "宁夏";
        }
        if (provinceName.contains("广西")) {
            return "广西";
        }
        return provinceName;
    }
    
    /**
     * 处理包含"辖"字的区域名称
     * @param regionName 区域名称
     * @return 处理后的区域名称
     */
    public static String handleRegionName(String regionName) {
        return regionName != null && regionName.contains("辖") ? "" : regionName;
    }
    
    /**
     * 生成有效期限
     * @return 格式化的有效期限字符串
     */
    public static String generateValidPeriod() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate startDate = LocalDate.now().minusYears(RANDOM.nextInt(10) + 1); // 1-10年前
        LocalDate endDate = startDate.plusYears(20); // 有效期20年
        return startDate.format(dateFormatter) + "-" + endDate.format(dateFormatter);
    }
}
