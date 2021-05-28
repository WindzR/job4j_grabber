package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;
import ru.job4j.html.SqlRuParse;

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
            Thread.sleep(30000);
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
            vacancies.forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
    }
}
