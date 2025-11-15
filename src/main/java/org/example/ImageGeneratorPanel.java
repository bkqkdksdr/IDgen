package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * 图片生成面板（生成图片按钮）。
 * - 提供“生成图片”按钮，点击后弹窗显示 src/fonts/empty.png 图片
 * - 支持图片加载失败的错误处理
 */
public class ImageGeneratorPanel extends JPanel {
    private final JButton generateImageButton;

    public ImageGeneratorPanel(JFrame parentFrame) {
        super(new FlowLayout(FlowLayout.LEFT));
        generateImageButton = new JButton("生成图片");
        
        generateImageButton.addActionListener(e -> {
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
            
            JLabel imageLabel = new JLabel(imageIcon);
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            imageFrame.getContentPane().add(scrollPane);
            
            // 设置窗口大小和位置
            imageFrame.setSize(800, 600);
            imageFrame.setLocationRelativeTo(parentFrame);
            imageFrame.setVisible(true);
        });
        
        add(generateImageButton);
    }
}