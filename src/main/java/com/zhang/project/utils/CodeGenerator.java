package com.zhang.project.utils;


import com.zhang.project.enumdata.CodePrefixEnum;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangyuhao
 * 通用编码返回
 */
public class CodeGenerator {
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private final Map<CodePrefixEnum, CodeCounter> counterMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化所有枚举值对应的计数器
        Arrays.stream(CodePrefixEnum.values()).forEach(prefix ->
                counterMap.put(prefix, new CodeCounter(getTodayDate()))
        );
    }

    private String getTodayDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 使用枚举生成编码
     */
    public String generate(CodePrefixEnum codePrefix) {
        CodeCounter counter = counterMap.get(codePrefix);
        if (counter == null) {
            counter = new CodeCounter(getTodayDate());
            counterMap.put(codePrefix, counter);
        }
        return counter.generateCode(codePrefix);
    }

    /**
     * 使用字符串前缀生成编码（向后兼容）
     */
    public String generate(String prefix) {
        CodePrefixEnum codePrefix = CodePrefixEnum.fromPrefix(prefix);
        return generate(codePrefix);
    }

    /**
     * 原来的方法保持兼容
     */
    public String generateFull() {
        return generate(CodePrefixEnum.SSQR);
    }

    /**
     * 内部计数器类
     */
    private static class CodeCounter {
        private String currentDate;
        private final AtomicInteger counter;

        public CodeCounter(String initialDate) {
            this.currentDate = initialDate;
            this.counter = new AtomicInteger(0);
        }

        public synchronized String generateCode(CodePrefixEnum codePrefix) {
            String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

            if (!today.equals(currentDate)) {
                currentDate = today;
                counter.set(0);
            }

            int nextValue = counter.incrementAndGet();
            int maxValue = (int) Math.pow(10, codePrefix.getSequenceLength()) - 1;

            if (nextValue > maxValue) {
                throw new IllegalStateException(
                        String.format("前缀 '%s' 的每日流水号已达上限（%d）",
                                codePrefix.getDescription(), maxValue)
                );
            }

            return codePrefix.getPrefix() + currentDate +
                    String.format(codePrefix.getFormat(), nextValue);
        }
    }
}
