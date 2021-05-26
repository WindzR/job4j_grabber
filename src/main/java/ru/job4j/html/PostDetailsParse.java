package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostDetailsParse {
    private String link = "https://www.sql.ru/forum/1336352/saratov-net-engineer-middle-full-time-60-80k";
    private List<String> details = new ArrayList<>();

    public PostDetailsParse() {
    }

    public PostDetailsParse(String link) {
        this.link = link;
    }

    public static void main(String[] args) {
        PostDetailsParse details = new PostDetailsParse();
        details.parsing(details.link);
    }

    public List<String> parsing(String parseLink) {
        try {
            Document doc = Jsoup.connect(parseLink).get();
            String description = description(doc);
            details.add(description);
//            System.out.println(description);
            String footer = dateParse(doc);
            String date = dateFind(footer);
            details.add(date);
//            System.out.println(date);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return details;
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
     * находит либо в формате ("dd-MMM-yy, hh:mm"), либо ("вчера/сегодня, hh:mm")
     * @param parse футер объявления
     * @return дату в формате сайта sql.ru.
     */
    private String dateFind(String parse) {
//        String date = "Error data search![dateFind]";
        String rsl = parse.substring(0, parse.indexOf("["));
//        Pattern patternS = Pattern.compile("\\d{2}\\s\\D{3}\\s\\d{2}\\W\\s\\d{2}\\W\\d{2}");
//        Pattern patternT = Pattern.compile("\\D{5,7}\\W\\s\\d{2}\\W\\d{2}");
//        Matcher matcherS = patternS.matcher(parse);
//        Matcher matcherT = patternT.matcher(parse);
//        while (matcherS.find()) {
//            date = parse.substring(matcherS.start(), matcherS.end());
//        }
        return rsl;
    }
}
