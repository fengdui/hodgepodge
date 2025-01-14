package com.hodgepodge.framework.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class MyTimeUtils {

    /**
     * 等到一分钟前的时间
     *
     * @return
     */
    private Date acquireData() {
        return new Date(LocalDateTime.now().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli() - 60 * 1000);
    }

    /**
     * date转localDate
     *
     * @return
     */
    public LocalDate dateToLocalDate() {
        return LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 时间间隔
     *
     * @return
     */
    public long interval() {
        return ChronoUnit.DAYS.between(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDate(),
                LocalDate.of(2020, 04, 30));
    }

    public void dayStart() {
        LocalDateTime with = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.ofHours(8)).with(LocalTime.MIN);
        System.out.println(with.toString());
        System.out.println(with.toEpochSecond(ZoneOffset.ofHours(8)));
    }

    public static void main(String[] args) {
        System.out.println(new MyTimeUtils().interval());
    }
}
