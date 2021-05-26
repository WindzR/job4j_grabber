package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private static final String LINK = "https://www.sql.ru/forum/job-offers/";
    private static int numsOfPage = 1;
    private int id = 1;

    public static void main(String[] args) {
        SqlRuParse sqlParse = new SqlRuParse();
        for (int pg = 1; pg < (numsOfPage + 1); pg++) {
            String page = String.valueOf(pg);
            sqlParse.list(LINK + page);
        }
    }

    /**
     * Метод парсит сайт с помощью утилиты jsoup и записывает данные в список List<Post>
     * для дальнейшей работы с БД PostgreSQL
     * @param parseLink ссылка источника для парсинга
     * @return List<Post> список объявлений
     */
    @Override
    public List<Post> list(String parseLink) {
        List<Post> postList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(parseLink).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                String link = href.attr("href");
                Post post1 = detail(link);
                post1.setIdPrimaryKey(id++);
//                System.out.println(link);
                post1.setLink(link);
                String heading = href.text();
                post1.setHeading(heading);
//                System.out.println(heading);
                Element dateMain = td.parent().child(5);
//                System.out.println(dateMain.text());
                postList.add(post1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        for (Post el : postList) {
            System.out.println(el);
        }
        return postList;
    }

    /**
     * Метод парсит определенное объявление по ссылке и достает текст объявления и его дату создания
     * @param link ссылка объявления
     * @return объект Post
     */
    @Override
    public Post detail(String link) {
        Post post = new Post();
        PostDetailsParse detailsParse = new PostDetailsParse(link);
        List<String> details = detailsParse.parsing(link);
        String description = details.get(0);
        post.setDetails(description);
        String sqlDate = details.get(1);
        SqlRuDateTimeParser date = new SqlRuDateTimeParser();
        LocalDateTime created = date.parse(sqlDate);
        post.setCreated(created);
        return post;
    }
}
