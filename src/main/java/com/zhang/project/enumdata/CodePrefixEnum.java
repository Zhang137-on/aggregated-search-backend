package com.zhang.project.enumdata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyuhao
 * 编码前缀的枚举值
 */
@SuppressWarnings("unused") // 抑制未使用警告
public enum CodePrefixEnum {

    /**
     * @deprecated 这是枚举值
     */
    SSQR("ssqr", "收款确认单", 4),
    AAA("aaa", "A类单据", 4),
    BBB("bbb", "B类单据", 4),
    CCC("ccc", "C类单据", 4),
    ORDER("order", "订单", 6),
    INV("inv", "发票", 5);

    private final String prefix;
    private final String description;
    private final int sequenceLength;

    CodePrefixEnum(String prefix, String description, int sequenceLength) {
        this.prefix = prefix;
        this.description = description;
        this.sequenceLength = sequenceLength;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return description;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public String getFormat() {
        return "%0" + sequenceLength + "d";
    }

    /**
     * 根据前缀字符串获取枚举值
     */
    public static CodePrefixEnum fromPrefix(String prefix) {
        for (CodePrefixEnum value : values()) {
            if (value.prefix.equals(prefix)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的前缀: " + prefix);
    }

    /**
     * 获取所有支持的编码前缀
     */
    public static List<String> getAllPrefixes() {
        return Arrays.stream(values())
                .map(CodePrefixEnum::getPrefix)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return prefix + " - " + description;
    }
}
