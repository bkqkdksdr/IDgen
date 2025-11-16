# 身份证号码生成器（Swing）

一个基于 Java Swing 的 18 位身份证号码生成工具，支持省/市/区三级联动选择、出生日期选择、性别选择、身份证号生成、一键复制，以及身份证信息图片生成功能。

## 功能特性

### 核心功能
- **省/市/区三级联动选择**：支持中文展示与区划编号生成，自动加载地址数据
- **出生日期选择**：使用 LGoodDatePicker 组件，默认日期为当前日期前 18 年，范围 1900-01-01 至 2099-12-31
- **性别选择**：男/女单选按钮，男性对应奇数顺序码，女性对应偶数顺序码
- **身份证号生成**：严格按照 GB 11643 规则生成 18 位身份证号码，包含地址码(6位)、出生日期码(8位)、顺序码(3位)和校验码(1位)
- **一键复制**：将生成的身份证号码快速复制到系统剪贴板

### 图片生成功能
- **身份证信息图片**：生成包含完整身份证信息的图片，包括：
  - 右上角头像显示
  - 随机生成的姓名（加粗显示）
  - 选择的性别和出生日期
  - 固定为汉族的民族信息
  - 完整的地址信息（省市区+随机详细地址，自动换行）
  - 生成的身份证号码（标准OCR字体）
  - 自动生成的签发机关信息
  - 自动计算的有效期限（1-10年前起始，20年有效期）
- **字体规范**：
  - 身份证号码：标准 OCR-B 10 BT 字体
  - 姓名、性别、民族、地址、签发机关：华文细黑字体
  - 出生日期、有效期限：常规华文细黑字体
- **自动换行**：地址信息自动换行显示，确保完整展示

## 技术栈与依赖

- **Java 8+**：基础开发语言
- **Swing**：图形用户界面框架
- **LGoodDatePicker**：专业的日期选择组件
- **Jackson**：JSON 数据解析
- **Apache POI**：Excel 文件处理（仅用于地址数据转换工具）
- **Maven**：项目构建与依赖管理

## 项目结构

```
src/
├── fonts/
│   ├── empty.png          # 身份证模板图片
│   ├── head.png           # 身份证头像图片
│   └── result_color.png   # 生成结果示例图片
├── main/
│   ├── java/org/example/
│   │   ├── Main.java                # 主程序入口与界面布局
│   │   ├── RegionSelectorPanel.java # 省/市/区选择面板
│   │   ├── BirthdayPickerPanel.java # 出生日期选择面板
│   │   ├── GenderSelectorPanel.java # 性别选择面板
│   │   ├── IdNumberUtil.java        # 身份证号码生成工具类
│   │   ├── AddressCodeUtil.java     # 地址数据加载与查询
│   │   ├── ExcelToAddressJson.java  # Excel转JSON辅助工具
│   │   └── Utils.java               # 通用工具类（字符串/数字处理）
│   └── resources/
│       └── output.json              # 省市区地址数据
└── test/
    └── java/                        # 测试代码
```

## 核心类职责

- **Main.java**：应用入口，负责整体界面布局和事件处理，包括身份证图片生成逻辑
- **RegionSelectorPanel.java**：省/市/区三级联动选择面板，提供地址信息获取方法
- **BirthdayPickerPanel.java**：出生日期选择面板，提供日期获取方法
- **GenderSelectorPanel.java**：性别选择面板，提供性别获取方法
- **IdNumberUtil.java**：身份证号码生成工具，实现 GB 11643 规则
- **AddressCodeUtil.java**：加载和查询地址数据，支持省市区联动
- **ExcelToAddressJson.java**：将 Excel 格式的地址数据转换为 JSON 格式
- **Utils.java**：通用工具类，包含姓名生成、地址生成、地址格式化、有效期限生成等字符串和数字处理方法

## 数据准备

### 地址数据文件
项目需要 `output.json` 文件来提供省市区地址数据，该文件应放置在 `src/main/resources/` 目录下。

**JSON 数据结构示例**：
```json
{
  "provinces": [
    {
      "name": "北京市",
      "code": "110000",
      "cities": [
        {
          "name": "北京市",
          "code": "110100",
          "districts": [
            {"name": "东城区", "code": "110101"},
            {"name": "西城区", "code": "110102"}
          ]
        }
      ]
    }
  ]
}
```

### 从 Excel 生成地址数据
如果需要从 Excel 文件生成 `output.json`，可以使用项目提供的辅助工具：

1. 准备 Excel 文件，包含三列数据：中文名、区划编号（6位 adcode）、城市编号（可选）
2. 运行转换工具：
   ```bash
   java org.example.ExcelToAddressJson 输入.xlsx 输出.json
   ```
3. 将生成的 `输出.json` 重命名为 `output.json` 并放入 `src/main/resources/` 目录

## 运行项目

### 方式一：在 IDE 中运行
直接运行主类 `org.example.Main`

### 方式二：使用 Maven 构建后运行

1. 构建项目：
   ```bash
   mvn -q -DskipTests package
   ```

2. 运行程序：
   ```bash
   java -cp target/classes org.example.Main
   ```

## 使用说明

1. **选择户籍地址**：从下拉菜单依次选择省、市、区
2. **选择出生日期**：点击日期选择器选择出生日期，默认显示18年前的今天
3. **选择性别**：点击单选按钮选择性别
4. **生成身份证号**：点击「生成身份证号」按钮生成18位身份证号码
5. **复制身份证号**：点击「复制」按钮将生成的身份证号码复制到剪贴板
6. **生成身份证图片**：点击「生成图片」按钮生成包含完整身份证信息的图片

## 代码示例

### 直接使用身份证号码生成工具

```java
// 6位地址码
String addressCode = "110101";
// 格式化的出生日期（yyyyMMdd）
String birth = IdNumberUtil.formatBirth(LocalDate.of(2000, 1, 1));
// 性别（true: 男, false: 女）
boolean male = true;
// 生成身份证号码
String idNumber = IdNumberUtil.generateIdNumber(addressCode, birth, male, null);

System.out.println("生成的身份证号码: " + idNumber);
```

### 自定义顺序码

```java
// 传入自定义顺序码（1-999），工具会根据性别自动修正奇偶
String customSequence = "123";
String idNumber = IdNumberUtil.generateIdNumber(addressCode, birth, male, customSequence);
```

### 使用工具类

```java
// 生成随机姓名
String name = Utils.generateRandomName();

// 生成详细地址
String address = Utils.generateDetailedAddress("北京市", "", "东城区");

// 简化省份名称
String simplifiedProvince = Utils.simplifyProvinceName("新疆维吾尔自治区");

// 处理区域名称
String handledCityName = Utils.handleRegionName("市辖区");

// 生成有效期限
String validPeriod = Utils.generateValidPeriod();
```

## 注意事项

1. **地址数据加载**：启动时若提示「未能加载地址数据」，请检查 `src/main/resources/output.json` 文件是否存在且格式正确
2. **字体支持**：身份证号码使用 OCR-B 10 BT 字体，若系统未安装此字体，会自动回退到默认字体
3. **图片显示**：生成的图片会缩小显示，以便完整展示在窗口中
4. **头像显示**：确保 `src/fonts/head.png` 文件存在，否则不会显示头像
5. **数据合法性**：本工具生成的身份证号码仅用于测试和学习，请勿用于非法用途

## 字体安装（可选）

为了获得最佳的身份证号码显示效果，建议安装 OCR-B 10 BT 字体：

1. 下载 OCR-B 10 BT 字体文件（.ttf 格式）
2. 安装字体：
   - Windows：复制到 `C:\Windows\Fonts` 目录
   - macOS：复制到 `~/Library/Fonts` 或 `/Library/Fonts` 目录
3. 重启应用程序后生效

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 贡献

欢迎提交 Issue 或 Pull Request 来优化代码和功能。