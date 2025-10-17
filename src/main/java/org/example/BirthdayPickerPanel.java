package org.example;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

/**
 * 出生日期选择面板（LGoodDatePicker）。
 * - 默认日期：当前日期的前 18 年（更贴近“刚成年”）
 * - 范围限制：1900-01-01 至 2099-12-31
 * - 不允许空日期
 * - 输入框宽度在首选大小基础上 +10px，避免弹窗遮挡
 */
public class BirthdayPickerPanel extends JPanel {
    private final JLabel birthdayLabel = new JLabel("出生日期：");
    private final DatePicker datePicker;

    public BirthdayPickerPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
        DatePickerSettings dpSettings = new DatePickerSettings(java.util.Locale.CHINA);
        dpSettings.setAllowEmptyDates(false);
        datePicker = new DatePicker(dpSettings);
        dpSettings.setDateRangeLimits(LocalDate.of(1900, 1, 1), LocalDate.of(2099, 12, 31));
        datePicker.setDate(LocalDate.now().minusYears(18));
        Dimension dpSize = datePicker.getPreferredSize();
        datePicker.setPreferredSize(new Dimension(dpSize.width + 10, dpSize.height));

        add(birthdayLabel);
        add(datePicker);
    }

    public LocalDate getDate() {
        return datePicker.getDate();
    }
}