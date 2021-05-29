package ru.job4j.grabber;

import com.sun.net.httpserver.HttpServer;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import ru.job4j.html.SqlRuParse;

import java.util.concurrent.Executors;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() {
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            return scheduler;
        } catch (SchedulerException ex) {
            System.out.println("Error scheduler start(Grabber.scheduler)");
            ex.printStackTrace();
        }
        return scheduler;
    }

    public void cfg() {
        try (InputStream in = new FileInputStream(new File("src/main/resources/app.properties"))) {
            cfg.load(in);
        } catch (IOException ex) {
            System.out.println("Error read file properties(Grabber.cfg)");
            ex.printStackTrace();
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        try {
            JobDetail job = newJob(GrabJob.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception ex) {
            System.out.println("Error in job scheduler(Grabber.init)");
            ex.printStackTrace();
        }
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> vacancies = parse.list("https://www.sql.ru/forum/job-offers/1");
            vacancies.forEach(store::save);
            List<Post> listDb = store.getAll();
            listDb.forEach(System.out::println);
        }
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes());
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

//    public void web(Store store) {
//        try {
//            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9000), 0);
//            server.createContext("/vacancies", exchange -> {
//                List<Post> posts = store.getAll();
//                StringJoiner html = new StringJoiner(System.lineSeparator());
//                html.add("<!DOCTYPE html>");
//                html.add("<html>");
//                html.add("<head>");
//                html.add("<meta charset=\"UTF-8\">");
//                html.add("<title>Vacancies</title>");
//                html.add("</head>");
//                html.add("<body>");
//
//                html.add("<table style=\"border: 1px solid black;\">");
//                html.add("<tr style=\"border: 1px solid black;\">");
//                html.add("<th style=\"border: 1px solid black;\">Name</th>");
//                html.add("<th style=\"border: 1px solid black;\">Date</th>");
//                html.add("<th style=\"border: 1px solid black;\">Description</th>");
//                html.add("</tr>");
//
//                for (Post post : posts) {
//                    html.add("<tr style=\"border: 1px solid black;\">");
//                    html.add(String.format("<td style=\"border: 1px solid black;\"><a href=\"%s\">%s</a></td>", post.getLink(), post.getLink()));
//                    html.add(String.format("<td style=\"border: 1px solid black;\">%s</td>", post.getCreated()));
//                    html.add(String.format("<td style=\"border: 1px solid black;\">%s</td>", post.getDetails()));
//                    html.add("</tr>");
//                }
//
//                html.add("</table>");
//
//                html.add("</body>");
//                html.add("</html>");
//
//                byte[] bytes = html.toString().getBytes(StandardCharsets.UTF_8);
//                exchange.getResponseHeaders().put("Content-Type", List.of("text/html", "charset=UTF-8"));
//                exchange.sendResponseHeaders(200, bytes.length);
//                try (OutputStream os = exchange.getResponseBody()) {
//                    os.write(bytes);
//                    os.flush();
//                }
//            });
//            server.setExecutor(Executors.newFixedThreadPool(10));
//            server.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
        System.out.println("<======================SERVER READY FOR LOADING INFORMATION=============================>");
        System.out.println("PLEASE, RUN THE COMMAND ---> curl -i http://localhost:9000/?msg=Hello");
        grab.web(store);
    }
}
