package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {
    private static final String LINK = "https://www.sql.ru/forum/job-offers/";
    private static int numsOfPage = 5;

    public static void main(String[] args) {
        for (int pg = 1; pg < (numsOfPage + 1); pg++) {
            String page = String.valueOf(pg);
            parsing(LINK + page);
        }
    }

    private static void parsing(String parseLink) {
        try {
            Document doc = Jsoup.connect(parseLink).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element date = td.parent().child(5);
                System.out.println(date.text());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
