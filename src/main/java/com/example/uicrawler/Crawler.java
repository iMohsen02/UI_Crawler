package com.example.uicrawler;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Crawler implements Runnable, Stoppable {
    private final Fetcher fetcher;
    private final Parser parser;
    private final Repository repository;
    private final URLs urls;
    private final UrlBlocker urlBlocker;


    private int maxPage;
    private final Report report;
    private static final AtomicBoolean stopCrawler = new AtomicBoolean(false);
    public static final String resources = "src/main/resources" + "/crawler_data/";
    public static final String path = Crawler.resources + Crawler.class.getName() + ".json";
    private URL seedHost;

    public Crawler(URL seed, int maxPage, UrlBlocker urlBlocker) throws IOException {
        File file = new File(resources);
        if (!file.exists()) //noinspection ResultOfMethodCallIgnored
            file.mkdir();
        this.urlBlocker = urlBlocker;
        this.fetcher = new Fetcher(urlBlocker);
        this.parser = new Parser(seed);
        Repository.init(Crawler.resources + "pages.xml");
        this.repository = Repository.getInstance();
        this.seedHost = seed;
        this.urls = new URLs(seed);
        this.maxPage = maxPage;
        this.report = new Report();
        stopCrawler.set(false);
    }


    public void crawl() throws InterruptedException {

        for (int i = report.getCrawledPageNum(); i < maxPage; i++) {
            if (stopCrawler.get()) break;
            crawlPage(new WebPage(urls.getNextURL()));
        }
    }

    private BlockingQueue<Integer> blockingQueue;

    public CompletableFuture<Void> crawlAsync() {
        return CompletableFuture.runAsync(() -> {
            try {

                blockingQueue = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors(), true);

                for (int i = report.getCrawledPageNum(); report.getCrawledPageNum() < maxPage - blockingQueue.size(); i++) {
                    System.out.println(i);
                    if (checkStop()) break;
                    blockingQueue.put(i);

                    crawlPageAsync(new WebPage(urls.getNextURL()))
                            .thenRun(blockingQueue::remove);

//                if (report.getCrawledPageNum() == maxPage - blockingQueue.size()) {
//                    stop();
//                    break;
//                }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(report);
        });
    }

    public boolean checkStop() {
        if (stopCrawler.get()) {
            while (!blockingQueue.isEmpty()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            return true;
        }
        return false;
    }


    private boolean crawlPage(@NotNull final WebPage page) {

        try {
            // fetch a page
            fetcher.fetch(page);

            if (page.getStatus() == WebPage.Status.CRAWLED) {
                // parse links of page
                var links = parser.parse(page);
                System.out.println("links:\t" + links.size());

                // add page urls to the list
                urls.addURL(links);

                // write the content of page
                repository.saveContent(page.generateFileTemplate());
            }
            // reports it
            report.addReport(page.getStatus());

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        System.out.println("page: " + page.getUrl() + "\tstatus: " + page.getStatus());
        return page.getStatus() == WebPage.Status.CRAWLED;
    }

    public CompletableFuture<Boolean> crawlPageAsync(WebPage page) {
        return CompletableFuture.supplyAsync(() -> crawlPage(page));
    }


    @Override
    public void run() {
        System.out.println("run crawler");
        crawlAsync();
    }

    @Override
    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("maxPage", maxPage);
        jsonObject.put("seedHost", seedHost.getHost());
        return jsonObject;
    }

    @Override
    public void init(JSONObject jsonObject) throws IOException {
        this.maxPage = Math.toIntExact((Long) jsonObject.get("maxPage"));
        this.seedHost = new URL("https://" + jsonObject.get("seedHost"));
        report.start();
        urls.start();
        urlBlocker.start();
    }

    @Override
    public boolean clearData() {
        return clearData(resources);
    }

    @Override
    public boolean stop() {
        System.out.println("crawler stop");

        Crawler.stopCrawler.set(true);
        checkStop();
        report.stop();
        urls.stop();
        urlBlocker.stop();

        System.out.println(report);
        return stop(path);
    }

    @Override
    public boolean start() {
        System.out.println("crawler starts");

        Crawler.stopCrawler.set(false);
        return start(path);
    }

    @Override
    public boolean checkStart() {
        return new File(path).exists();
    }

    @Override
    public String toString() {
        return report.toString();
    }

    public Report getReport() {
        return report;
    }

    public int getMaxPage() {
        return maxPage;
    }

}
