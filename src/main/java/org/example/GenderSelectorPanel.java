package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * 性别选择面板（男/女单选）。
 * - 默认勾选：男
 * - 提供 isMale() 方法供外部逻辑判断奇偶顺序码
 */
public class GenderSelectorPanel extends JPanel {
    private final JLabel genderLabel = new JLabel("性别：");
    private final JRadioButton maleRadio = new JRadioButton("男", true);
    private final JRadioButton femaleRadio = new JRadioButton("女");

    public GenderSelectorPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        add(genderLabel);
        add(maleRadio);
        add(femaleRadio);
    }

    public boolean isMale() {
        return maleRadio.isSelected();
    }
}