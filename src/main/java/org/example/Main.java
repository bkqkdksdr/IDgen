package org.example;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.datatransfer.StringSelection;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
 * 功能：身份证号码生成器（18位）。
 * 最新说明与变更：
 * - 出生日期改用 LGoodDatePicker（基于 java.time.LocalDate）。
 * - 默认日期为“当前日期减 8 年”，更贴近“刚成年”场景；范围限制 1900-01-01 至 2099-12-31；不允许空日期。
 * - 布局：主面板使用 GridLayout(6,1)，每一行使用 FlowLayout 左对齐；窗口高度加至 650 以给日历弹窗足够空间。
 * - 地址数据：从类路径 resources/output.json 加载，提供省/市/区三级联动。
 * - 生成规则：地址码(6) + 生日(yyyyMMdd) + 顺序码(3 位，奇数男/偶数女) + 校验码(GB 11643)。
 * - 支持“复制”按钮，直接放入系统剪贴板。
 */
public class Main {
    private static JComboBox<Integer> yearCombo;
    private static JComboBox<Integer> monthCombo;
    private static JComboBox<Integer> dayCombo;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("身份证号码生成器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650); // 加宽并加高窗口，避免日期弹窗溢出
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 步骤 1：户籍地址（抽取为 RegionSelectorPanel）
        RegionSelectorPanel regionSelector = new RegionSelectorPanel(frame);
        // 步骤 2：出生日期（抽取为 BirthdayPickerPanel）
        BirthdayPickerPanel birthdayPanel = new BirthdayPickerPanel();
        // 步骤 3：性别选择（抽取为 GenderSelectorPanel）
        GenderSelectorPanel genderPanel = new GenderSelectorPanel();

        // 5) 生成按钮与结果
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("生成身份证号");
        JTextField resultField = new JTextField(18); // 18位宽度
        resultField.setEditable(false);
        actionPanel.add(generateButton);
        actionPanel.add(resultField);
        // 复制按钮
        JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton copyButton = new JButton("复制");
        JLabel copyStatusLabel = new JLabel("");
        copyButton.addActionListener(e -> {
            String text = resultField.getText();
            if (text != null && !text.isEmpty()) {
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new java.awt.datatransfer.StringSelection(text), null);
                copyStatusLabel.setText("复制成功");
            }
        });
        copyPanel.add(copyButton);
        copyPanel.add(copyStatusLabel);

        // 6) 结果说明
        JPanel tipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipPanel.add(new JLabel("提示：地址展示中文，生成按区划编号。"));

        panel.add(regionSelector);
        panel.add(birthdayPanel);
        panel.add(genderPanel);
        panel.add(actionPanel);
        panel.add(copyPanel);
        panel.add(tipPanel);

        frame.setContentPane(panel);
        frame.setVisible(true);

        // 生成逻辑
        generateButton.addActionListener(e -> {
            copyStatusLabel.setText("");

            String provinceName = regionSelector.getProvinceName();
            String cityName = regionSelector.getCityName();
            String districtName = regionSelector.getDistrictName();
            java.time.LocalDate selected = birthdayPanel.getDate();
            boolean male = genderPanel.isMale();
            if (provinceName == null || cityName == null) {
                JOptionPane.showMessageDialog(frame, "请选择省、市（区可选）", "缺少地址", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selected == null) {
                JOptionPane.showMessageDialog(frame, "请选择出生日期", "缺少日期", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String addressCode = AddressCodeUtil.getFullAddressCode(provinceName, cityName, districtName);
            if (addressCode == null || addressCode.length() != 6) {
                JOptionPane.showMessageDialog(frame, "地址码无效，请检查 output.json", "地址错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String birthStr = IdNumberUtil.formatBirth(selected);
            String id = IdNumberUtil.generateIdNumber(addressCode, birthStr, male, null);
            resultField.setText(id);
        });
    }


}