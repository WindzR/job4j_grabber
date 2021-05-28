package ru.job4j.grabber;

import ru.job4j.quartz.AlertRabbit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            String url = cfg.getProperty("jdbc.url");
            String login = cfg.getProperty("jdbc.username");
            String password = cfg.getProperty("jdbc.password");
            cnn = DriverManager.getConnection(url, login, password);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * метод save сохраняет модель данных post в БД PostgreSQL
     * в качестве даты используется класс Timestamp without time zone -
     * что недопустимо в product development(в большинстве случаев),
     * но т.к. у нас учебный курс, то и так сойдет!(=])
     * @param post модель данных Post
     */
    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, post.getHeading());
            statement.setString(2, post.getDetails());
            statement.setString(3, post.getLink());
            Timestamp timestamp = Timestamp.valueOf(post.getCreated());
            statement.setTimestamp(4, timestamp);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setIdPrimaryKey(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Возвращает список всех объявлений posts из БД
     * @return List<Post>
     */
    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("SELECT * FROM post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        Post result = null;
        String sql = "SELECT * FROM post WHERE id = ?";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties cfg = new Properties();
        ClassLoader loader = AlertRabbit.class.getClassLoader();
        try (InputStream io = loader.getResourceAsStream("rabbit.properties")) {
            cfg.load(io);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        PsqlStore store = new PsqlStore(cfg);
//        for (int i = 1; i <= 6; i++) {
//            String pg = String.valueOf(i + 10);
//            Post post = new Post(1, "Heading", "Some text", "https://www.sql.ru/forum/job-offers/" + pg, LocalDateTime.of(2021, 5, (i + 1), 8, i));
//            store.save(post);
//        }
        List<Post> list = store.getAll();
        for (Post post: list) {
            System.out.println(post);
        }
        Post test = store.findById(String.valueOf(2));
        System.out.println(test);
    }
}
