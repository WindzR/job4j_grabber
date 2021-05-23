package ru.job4j.grabber.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SqlRuDateTimeParserTest {

    @Test
    public void whenParseJanuary() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDateTime rsl = parser.parse("22 янв 16, 10:56");
        LocalDateTime expected = LocalDateTime.of(2016, 1, 22, 10, 56);
        assertThat(rsl, is(expected));
    }

    @Test
    public void whenParseDecember() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDateTime rsl = parser.parse("31 дек 20, 23:59");
        LocalDateTime expected = LocalDateTime.of(2020, 12, 31, 23, 59);
        assertThat(rsl, is(expected));
    }

    @Test
    public void whenParseToday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDateTime rsl = parser.parse("сегодня, 12:56");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime expected = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 12, 56);
        assertThat(rsl, is(expected));
    }

    @Test
    public void whenParseYesterday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDateTime rsl = parser.parse("вчера, 08:45");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime expected = LocalDateTime.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), 8, 45);
        assertThat(rsl, is(expected));
    }
}