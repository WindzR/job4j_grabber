package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SqlRuDateTimeParser implements DateTimeParser {
    static final List<String> MONTHS = List.of("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек");

    @Override
    public LocalDateTime parse(String parse) {
        if (parse.contains("сегодня") || parse.contains("вчера")) {
            return today(parse);
        }
        String[] strings = parse.split("[ ,:]");
        int month = getMonth(strings[1]);
        List<Integer> numbers = parseNums(strings);
        int year = numbers.get(1) + 2000;
        return LocalDateTime.of(year, month, numbers.get(0), numbers.get(2), numbers.get(3));
    }

    private LocalDateTime today(String parse) {
        LocalDateTime todayDate = LocalDateTime.now();
        LocalDateTime parseDate = parse.contains("сегодня") ? todayDate : todayDate.minusDays(1);
        int year = parseDate.getYear();
        int month = parseDate.getMonthValue();
        int dayOfMonth = parseDate.getDayOfMonth();
        String[] strings = parse.split("[ ,:]");
        List<Integer> numbers = parseNums(strings);
        return LocalDateTime.of(year, month, dayOfMonth, numbers.get(0), numbers.get(1));
    }

    /**
     * Метод получает массив Strings и возвращает список Integer для дальнейшей работы с датой
     * @param strings входящий поток стрингов
     * @return List<Integer> (данные для использования в LocalDateTime).
     */
    private List<Integer> parseNums(String[] strings) {
        return Arrays.stream(strings)
                .filter(el -> el.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * Метод получает номер месяца для LocalDateTime
     * @param month принимает часть строки с датой, где содержится месяц на кириллице
     * @return порядковый номер месяца для LocalDateTime, допустимое значение [1-12].
     */
    private int getMonth(String month) {
        int result = MONTHS.stream()
               .filter(el -> el.equals(month))
                .mapToInt(el -> MONTHS.indexOf(month))
                .findFirst().orElse(-1);
        return (result >= 0 && result <= 11) ? result + 1 : 0;
    }
}
