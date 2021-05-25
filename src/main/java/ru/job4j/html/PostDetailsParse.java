package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostDetailsParse {
    private static final String LINK = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";

    public static void main(String[] args) {
        var details = new PostDetailsParse();
        details.parsing(LINK);
    }

    private void parsing(String parseLink) {
        try {
            Document doc = Jsoup.connect(parseLink).get();
            String description = description(doc);
            System.out.println(description);
            String footer = dateParse(doc);
            String date = dateFind(footer);
            System.out.println(date);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Метод парсит текст объявления по ссылке с сайта sql.ru/forum
     * @param doc документ распаршенный jsoup
     * @return текст объявления.
     */
    private String description(Document doc) {
        String description = "Error data parsing![description]";
        Elements msg = doc.getElementsByClass("msgBody").get(0).getElementsByTag("td");
        for (Element td : msg) {
            Element element = td.parent().child(1);
            description = element.text();
        }
        return description;
    }

    /**
     * Метод парсит футер объявления по ссылке с сайта sql.ru/forum
     * @param doc документ распаршенный jsoup
     * @return футер объявления.
     */
    private String dateParse(Document doc) {
        String dateFooter = "Error data parsing![dateParse]";
        Elements footer = doc.getElementsByClass("msgFooter").get(0).getElementsByTag("td");
        for (Element td : footer) {
            Element footerText = td.parent();
            dateFooter = footerText.text();
        }
        return dateFooter;
    }

    /**
     * Находит дату объявления по определенной маске в футере объявления
     * @param parse футер объявления
     * @return дату в формате сайта sql.ru.
     */
    private String dateFind(String parse) {
        String date = "Error data search![dateFind]";
        Pattern pattern = Pattern.compile("\\d{2}\\s\\D{3}\\s\\d{2}\\W\\s\\d{2}\\W\\d{2}");
        Matcher matcher = pattern.matcher(parse);
        while (matcher.find()) {
            date = parse.substring(matcher.start(), matcher.end());
        }
        return date;
    }
}
