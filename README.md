# 身份证号码生成器（Swing）

一个基于 Java Swing 的 18 位身份证号码生成工具。提供省/市/区三级联动、LGoodDatePicker 出生日期选择、性别单选、按 GB 11643 规则计算校验码，并支持一键复制到剪贴板。

## 功能概览
- 省/市/区三级联动选择，支持中文展示与区划编号生成。
- 出生日期选择（LGoodDatePicker）：默认“今天 - 18 年”，范围 1900-01-01 至 2099-12-31，不允许空日期。
- 性别选择：男/女单选（奇数男、偶数女）。
- 身份证号生成：地址码(6) + 生日(yyyyMMdd) + 顺序码(3) + 校验码(1)。
- 复制按钮：将生成的号码放入系统剪贴板。
- UI 优化：窗口大小 `900x650`，日期输入框宽度在首选基础上 +10px，避免弹窗遮挡。

## 技术栈与依赖
- Java 8+ / Swing
- LGoodDatePicker（日期选择器）
- Jackson（JSON 解析）
- Apache POI（仅用于 Excel 转 JSON 的辅助工具）
- Maven（项目构建）

## 项目结构与职责
- `org.example.Main`：应用入口与总体布局（GridLayout 6 行）。
  - 步骤顺序：
    1) 户籍地址（`RegionSelectorPanel`）
    2) 出生日期（`BirthdayPickerPanel`）
    3) 性别选择（`GenderSelectorPanel`）
    4) 生成按钮与结果显示
    5) 复制按钮与状态提示
    6) 提示信息（结果说明）
- `org.example.RegionSelectorPanel`：省/市/区三级联动选择面板，固定下拉框宽度，联动刷新。
- `org.example.BirthdayPickerPanel`：出生日期选择面板（LGoodDatePicker），默认日期与范围设置，宽度微调。
- `org.example.GenderSelectorPanel`：性别单选面板（男默认）。
- `org.example.IdNumberUtil`：18 位身份证号生成工具（含校验码与生日格式化）。
- `org.example.AddressCodeUtil`：地址数据加载与查询（类路径 `resources/output.json`）。
- `org.example.ExcelToAddressJson`：将三列 Excel 转为联动所需 JSON 的命令行工具。

## 数据准备
- 将 `output.json` 放置到类路径 `src/main/resources/`。文件结构示例：
  ```json
  {"provinces":[
    {"name":"某省","code":"110000","cities":[
      {"name":"某市","code":"110100","districts":[
        {"name":"某区","code":"110101"}
      ]}
    ]}
  ]}
  ```
- 若需要从 Excel 生成：
  - 输入表头三列：中文名、区划编号 `adcode(6位)`, 城市编号 `citycode`（可选）。
  - 运行：`java org.example.ExcelToAddressJson 输入.xlsx 输出.json`
  - 生成后的 `输出.json` 放入 `src/main/resources/output.json`。

## 运行方式
- 在 IDE 中直接运行主类：`org.example.Main`。
- 或使用 Maven 构建后运行（示例）：
  - `mvn -q -DskipTests package`
  - 构建完成后，在 IDE 以 `target/classes` 为类路径运行 `org.example.Main`（根据你的环境设置类路径）。

## 使用说明（与 UI 步骤一致）
- 1) 选择户籍地址：按省 -> 市 -> 区联动；生成使用区县编号优先，其次城市编号前 6 位，最后省编号。
- 2) 选择出生日期：默认“今天 - 18 年”；范围 1900-2099；禁止空日期。
- 3) 选择性别：男/女（决定顺序码奇偶）。
- 4) 点击“生成身份证号”：自动生成 18 位号码。
- 5) 点击“复制”：复制到系统剪贴板。
- 6) 提示：界面展示中文地址，生成按区划编号。

## 代码示例（工具类直接使用）
```java
String addressCode = "110101"; // 6位地址码
String birth = org.example.IdNumberUtil.formatBirth(java.time.LocalDate.of(2000, 1, 1));
boolean male = true; // 男性
String id = org.example.IdNumberUtil.generateIdNumber(addressCode, birth, male, null);
```

## 常见问题
- 未能加载地址数据：启动时界面会提示“未能加载地址数据”，请确认 `src/main/resources/output.json` 已存在且格式正确。
- 日期弹窗被遮挡：已通过加高窗口与增加输入框宽度处理；如需进一步优化，可在 UI 中调整弹窗定位策略。
- 顺序码自定义：`generateIdNumber` 第四参数可传入 `1-999` 的字符串，工具会按性别自动修正奇偶并做边界处理。

## 贡献与许可
- 欢迎提交 Issue 或 PR 优化代码与数据结构。
- 许可证：根据仓库实际设置，如未声明则默认保留所有权利。