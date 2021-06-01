package ru.job4j.grabber;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PsqlStoreTest {

    public Connection init() {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("jdbc.driver"));
            return DriverManager.getConnection(
                    config.getProperty("jdbc.url"),
                    config.getProperty("jdbc.username"),
                    config.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void whenSaveItem() {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post post = new Post(1, "heading", "Text of vacancy"
                    , "https://www.sql.ru/forum/job-offers/3"
                    , LocalDateTime.of(2021, 5, 1, 0, 0));
            store.save(post);
            List<Post> list = store.getAll();
            assertThat(list.get(0), is(post));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void whenSaveThenSearchById() {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post post = new Post(1, "heading", "Text of vacancy"
                    , "https://www.sql.ru/forum/job-offers/3"
                    , LocalDateTime.of(2021, 5, 1, 0, 0));
            store.save(post);
            List<Post> list = store.getAll();
            int searchId = list.get(0).getIdPrimaryKey();
            Post search = store.findById(String.valueOf(searchId));
            assertThat(search, is(post));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}