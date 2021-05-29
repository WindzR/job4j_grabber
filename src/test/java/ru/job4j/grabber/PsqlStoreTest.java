package ru.job4j.grabber;

import org.junit.Test;
import java.sql.Connection;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

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
    }
}