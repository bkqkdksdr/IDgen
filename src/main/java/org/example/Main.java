package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
 * - 支持“生成图片”按钮，点击后弹出新窗口显示 src/fonts/empty.png 图片。
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

        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
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
        copyPanel.add(copyButton);
        copyPanel.add(copyStatusLabel);
        
        // 生成图片按钮
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateImageButton = new JButton("生成图片");
        JLabel imageimageStatusLabel = new JLabel("");
        imagePanel.add(generateImageButton);
        imagePanel.add(imageimageStatusLabel);
        
        // 6) 结果说明
        JPanel tipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipPanel.add(new JLabel("提示：地址展示中文，生成按区划编号。"));

        panel.add(regionSelector);
        panel.add(birthdayPanel);
        panel.add(genderPanel);
        panel.add(actionPanel);
        panel.add(copyPanel);
        panel.add(imagePanel);
        panel.add(tipPanel);
        frame.setContentPane(panel);
        frame.setVisible(true);

        // 生成逻辑
        generateButton.addActionListener(e -> {
            copyStatusLabel.setText("");
            imageimageStatusLabel.setText("");

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

        //复制逻辑
        copyButton.addActionListener(e -> {
            String text = resultField.getText();
            if (text != null && !text.isEmpty()) {
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new java.awt.datatransfer.StringSelection(text), null);
                copyStatusLabel.setText("复制成功");
            }else{
                copyStatusLabel.setText("请先生成身份证号");
            }
        });

        //生成图片逻辑
        generateImageButton.addActionListener(e -> {
            // 检查是否先生成了身份证号
            String id = resultField.getText();
            if (id == null || id.isEmpty()) {
                imageimageStatusLabel.setText("请先生成身份证号");
                return;
            }
            
            // 获取身份证信息
            String provinceName = regionSelector.getProvinceName();
            provinceName = Utils.simplifyProvinceName(provinceName);
            String cityName = regionSelector.getCityName();
            cityName = Utils.handleRegionName(cityName);
            String districtName = regionSelector.getDistrictName();
            districtName = Utils.handleRegionName(districtName);
            LocalDate birthDate = birthdayPanel.getDate();
            boolean isMale = genderPanel.isMale();
            
            // 生成随机姓名
            String name = Utils.generateRandomName();
            
            // 生成详细地址
            String address = Utils.generateDetailedAddress(provinceName, cityName, districtName);
            
            // 创建新窗口显示图片
            JFrame imageFrame = new JFrame("生成图片");
            imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // 加载fonts文件夹下的图片
            String imagePath = System.getProperty("user.dir") + "/src/fonts/empty.png";
            ImageIcon imageIcon = new ImageIcon(imagePath);
            
            // 检查图片是否成功加载
            if (imageIcon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                JOptionPane.showMessageDialog(imageFrame, "图片加载失败: " + imagePath, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 获取原始图片
            Image originalImage = imageIcon.getImage();
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            
            // 创建缓冲图像以便进行绘制操作
            BufferedImage bufferedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            
            // 绘制原始图片
            g2d.drawImage(originalImage, 0, 0, null);
            
            // 设置绘制文本的样式
            g2d.setColor(Color.BLACK); // 黑色文本
            
            // 绘制头像
            String headImagePath = System.getProperty("user.dir") + "/src/fonts/head.png";
            ImageIcon headIcon = new ImageIcon(headImagePath);
            if (headIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                Image headImage = headIcon.getImage();
                // 设置头像大小和位置（调整这些值以适应身份证模板）
                int headWidth = 400; // 头像宽度
                int headHeight = 500; // 头像高度
                int headX = originalWidth - headWidth  - 180; // 头像X坐标
                int headY = originalHeight - headHeight - 1900; // 头像Y坐标
                g2d.drawImage(headImage, headX, headY, headWidth, headHeight, null);
            }
            
            // 绘制姓名
            g2d.setFont(new Font("华文细黑", Font.BOLD, 70));
            FontMetrics metrics = g2d.getFontMetrics();
            int nameWidth = metrics.stringWidth(name);
            int nameX = (originalWidth - nameWidth) / 2 - 520; // 调整位置
            int nameY = originalHeight - 2365; // 调整位置
            g2d.drawString(name, nameX, nameY);
            
            // 绘制性别
            String gender = isMale ? "男" : "女";
            g2d.setFont(new Font("华文细黑", Font.BOLD, 60));
            metrics = g2d.getFontMetrics();
            int genderWidth = metrics.stringWidth(gender);
            int genderX = (originalWidth - genderWidth) / 2 - 560; // 调整位置
            int genderY = originalHeight - 2225; // 调整位置
            g2d.drawString(gender, genderX, genderY);
            
            // 绘制民族
            String nation = "汉";
            g2d.setFont(new Font("华文细黑", Font.BOLD, 60));
            metrics = g2d.getFontMetrics();
            int nationWidth = metrics.stringWidth(nation);
            int nationX = (originalWidth - nationWidth) / 2 - 155; // 调整位置
            int nationY = originalHeight - 2225; // 调整位置
            g2d.drawString(nation, nationX, nationY);
            
            // 绘制出生日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy           MM       dd");
            String birthDateStr = birthDate.format(formatter);
            g2d.setFont(new Font("华文细黑", Font.PLAIN, 60));
            metrics = g2d.getFontMetrics();
            int birthWidth = metrics.stringWidth(birthDateStr);
            int birthX = (originalWidth - birthWidth) / 2 - 305; // 调整位置
            int birthY = originalHeight - 2090; // 调整位置
            g2d.drawString(birthDateStr, birthX, birthY);
            
            // 绘制地址
            g2d.setFont(new Font("华文细黑", Font.BOLD, 60));
            metrics = g2d.getFontMetrics();
            int addressX = (originalWidth - metrics.stringWidth(provinceName)) / 2 - 500; // 调整位置
            int addressY = originalHeight - 1950; // 调整位置
            
            // 地址可能需要换行显示
            StringBuilder line = new StringBuilder();
            for (char c : address.toCharArray()) {
                if (metrics.stringWidth(line.toString() + c) > 900) { // 每行最大宽度
                    g2d.drawString(line.toString(), addressX, addressY);
                    line.setLength(0);
                    addressY += 80; // 行间距
                }
                line.append(c);
            }
            if (line.length() > 0) {
                g2d.drawString(line.toString(), addressX, addressY);
            }
            
            // 绘制身份证号码
            g2d.setFont(new Font("OCR-B 10 BT", Font.PLAIN, 90)); // 设置字体为身份证号码标准字体OCR-B 10 BT
            metrics = g2d.getFontMetrics();
            int idWidth = metrics.stringWidth(id);
            int idX = (originalWidth - idWidth) / 2 + 140; // 保持原有位置
            int idY = originalHeight - metrics.getHeight() - 1490; // 保持原有位置
            g2d.drawString(id, idX, idY);
            
            // 绘制签发机关
            String issuingAuthority = provinceName + cityName + districtName + "分局";
            g2d.setFont(new Font("华文细黑", Font.BOLD, 60));
            metrics = g2d.getFontMetrics();
            int authorityWidth = metrics.stringWidth(issuingAuthority);
            int authorityX = (originalWidth - authorityWidth) / 2 + 60; // 调整位置
            int authorityY = originalHeight - 310; // 调整位置
            g2d.drawString(issuingAuthority, authorityX, authorityY);
            
            // 绘制有效期限
            String validPeriod = Utils.generateValidPeriod();
            g2d.setFont(new Font("华文细黑", Font.PLAIN, 60));
            metrics = g2d.getFontMetrics();
            int periodWidth = metrics.stringWidth(validPeriod);
            int periodX = (originalWidth - periodWidth) / 2 +  125; // 调整位置
            int periodY = originalHeight - 170; // 调整位置
            g2d.drawString(validPeriod, periodX, periodY);
            
            // 释放资源
            g2d.dispose();
            
            // 将绘制后的图片缩小到一半
            int newWidth = originalWidth / 2;
            int newHeight = originalHeight / 2;
            Image scaledImage = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            // 创建标签和滚动面板
            JLabel imageLabel = new JLabel(scaledIcon);
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            imageFrame.getContentPane().add(scrollPane);
            
            // 设置窗口大小为缩小后的图片尺寸加上边距
            imageFrame.setSize(newWidth + 40, newHeight + 40);
            imageFrame.setLocationRelativeTo(frame);
            imageFrame.setVisible(true);
        });
    }
}