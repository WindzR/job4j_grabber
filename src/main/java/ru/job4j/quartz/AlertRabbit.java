package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {
    Properties properties;
    Connection connect;

    /**
     * Читаем файл конфигурации и устанавливаем соединение с БД Postgre
     * @return возвращает объект Connection.
     */
    private Connection initConnection() {
        try {
            properties = new Properties();
            ClassLoader loader = AlertRabbit.class.getClassLoader();
            try (InputStream io = loader.getResourceAsStream("rabbit.properties")) {
                properties.load(io);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            connect = null;
            Class.forName(properties.getProperty("jdbc.driver"));
            String url = properties.getProperty("jdbc.url");
            String login = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            connect = DriverManager.getConnection(url, login, password);
            if (connect != null) {
                System.out.println("Connection established...");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return connect;
    }

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();
        try (Connection cn = rabbit.initConnection()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println("Connection is closed...");
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit(created_date) VALUES (?)", Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setTimestamp(1, timestamp);
                statement.executeUpdate();
                System.out.println("Дата: " + calendar.getTime());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}
