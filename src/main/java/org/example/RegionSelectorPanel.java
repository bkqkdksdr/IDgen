package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * 省/市/区三级联动选择面板（用于 Main 的步骤 1）。
 * - 布局：FlowLayout 左对齐，间距紧凑（8px 水平，0px 垂直）。
 * - 宽度策略：计算各级最长名称，使用 prototypeDisplayValue 固定三个下拉框的显示宽度，避免随内容变化导致抖动。
 * - 数据来源：依赖 AddressCodeUtil 从类路径 resources/output.json 加载并缓存省/市/区数据（静态映射）。
 * - 初始值：默认选中第一个省、市、区（若有）。
 * - 联动逻辑：选择省后刷新市；选择市后刷新区。
 * - 对外方法：getProvinceName()/getCityName()/getDistrictName() 返回当前选择的中文名称。
 */
public class RegionSelectorPanel extends JPanel {
    private final JLabel regionLabel = new JLabel("户籍地址：");
    private final JComboBox<String> provinceCombo = new JComboBox<>();
    private final JComboBox<String> cityCombo = new JComboBox<>();
    private final JComboBox<String> districtCombo = new JComboBox<>();

    public RegionSelectorPanel(JFrame parentFrame) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 0));
        add(regionLabel);
        add(provinceCombo);
        add(cityCombo);
        add(districtCombo);

        // 加载地址数据
        AddressCodeUtil.load();
        List<String> provinces = AddressCodeUtil.getProvinceNames();
        if (provinces.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "未能加载地址数据，请检查 resources/output.json",
                    "数据缺失", JOptionPane.ERROR_MESSAGE);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            System.exit(1);
            return;
        }

        for (String p : provinces) provinceCombo.addItem(p);
        // 计算各级最长项用于固定宽度
        String protoProvince = longestString(AddressCodeUtil.PROVINCES.values());
        String protoCity = longestStringFromNestedMap(AddressCodeUtil.CITIES);
        String protoDistrict = longestStringFromNestedMap(AddressCodeUtil.DISTRICTS);
        if (protoProvince.isEmpty()) protoProvince = "请选择省";
        if (protoCity.isEmpty()) protoCity = "请选择市";
        if (protoDistrict.isEmpty()) protoDistrict = "请选择区";
        provinceCombo.setPrototypeDisplayValue(protoProvince);
        cityCombo.setPrototypeDisplayValue(protoCity);
        districtCombo.setPrototypeDisplayValue(protoDistrict);

        // 默认展示第一个省/市/区
        provinceCombo.setSelectedIndex(0);
        String pName0 = (String) provinceCombo.getItemAt(0);
        String pCode0 = AddressCodeUtil.getProvinceCode(pName0);
        cityCombo.removeAllItems();
        List<String> cities0 = AddressCodeUtil.getCityNames(pCode0);
        for (String c0 : cities0) cityCombo.addItem(c0);
        if (cityCombo.getItemCount() > 0) {
            cityCombo.setSelectedIndex(0);
            String cName0 = (String) cityCombo.getItemAt(0);
            String cCode0 = AddressCodeUtil.getCityCode(pCode0, cName0);
            districtCombo.removeAllItems();
            List<String> dists0 = AddressCodeUtil.getDistrictNames(cCode0);
            for (String d0 : dists0) districtCombo.addItem(d0);
            if (districtCombo.getItemCount() > 0) {
                districtCombo.setSelectedIndex(0);
            }
        }

        // 联动：省 -> 市
        provinceCombo.addActionListener(e -> {
            cityCombo.removeAllItems();
            districtCombo.removeAllItems();
            String pName = (String) provinceCombo.getSelectedItem();
            if (pName == null) return;
            String pCode = AddressCodeUtil.getProvinceCode(pName);
            List<String> cities = AddressCodeUtil.getCityNames(pCode);
            for (String c : cities) cityCombo.addItem(c);
        });

        // 联动：市 -> 区
        cityCombo.addActionListener(e -> {
            districtCombo.removeAllItems();
            String pName = (String) provinceCombo.getSelectedItem();
            String cName = (String) cityCombo.getSelectedItem();
            if (pName == null || cName == null) return;
            String pCode = AddressCodeUtil.getProvinceCode(pName);
            String cCode = AddressCodeUtil.getCityCode(pCode, cName);
            List<String> districts = AddressCodeUtil.getDistrictNames(cCode);
            for (String d : districts) districtCombo.addItem(d);
        });
    }

    public String getProvinceName() {
        return (String) provinceCombo.getSelectedItem();
    }
    public String getCityName() {
        return (String) cityCombo.getSelectedItem();
    }
    public String getDistrictName() {
        return (String) districtCombo.getSelectedItem();
    }

    private static String longestString(Iterable<String> values) {
        String longest = "";
        for (String s : values) {
            if (s != null && s.length() > longest.length()) longest = s;
        }
        return longest;
    }
    private static String longestStringFromNestedMap(Map<String, Map<String, String>> nested) {
        String longest = "";
        for (Map<String, String> m : nested.values()) {
            for (String s : m.values()) {
                if (s != null && s.length() > longest.length()) longest = s;
            }
        }
        return longest;
    }
}