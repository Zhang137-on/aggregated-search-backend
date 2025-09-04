package com.zhang.project.utils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangyuhao
 * 用以生成编码
 */
@Service
public class CodeUtil {
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private String currentDate;
    private AtomicInteger counter;

    @PostConstruct
    public void init() {
        this.currentDate = getTodayDate();
        this.counter = new AtomicInteger(0);
    }

    private String getTodayDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public synchronized String generateFull() {
        String today = getTodayDate();

        if (!today.equals(currentDate)) {
            currentDate = today;
            counter.set(0);
        }

        int nextValue = counter.incrementAndGet();
        if (nextValue > 9999) {
            throw new IllegalStateException("每日流水号已达上限（9999）");
        }
        String PREFIX = "ssqr";
        return PREFIX + currentDate + String.format("%04d", nextValue);
    }
}
