package com.example.uicrawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;

public class URLs implements Stoppable {
    private final Queue<URL> urls;

    public Queue<URL> getUrls() {
        return urls;
    }

    private final UniquenessChecker uniquenessChecker;
    public static final String path = Crawler.resources + URLs.class.getName() + ".json";

    {
        urls = new ConcurrentLinkedDeque<>();
        uniquenessChecker = new UniquenessChecker();
    }

    public URLs(URL seed) {
        this.addURL(List.of(seed));
    }

    public URLs() {
    }

    @SuppressWarnings("BusyWait")
    public URL getNextURL() throws InterruptedException {
        System.out.println("size urls:\t" + urls.size());
        // todo check empty lists
        while (this.urls.isEmpty()) Thread.sleep(1_000L);
        return this.urls.remove();
    }

    public void addURL(List<URL> links) {

        List<URL> list = links.stream()
                .filter(uniquenessChecker::check)
                .toList();
        System.out.println("unique links:\t" + list.size());
        this.urls.addAll(list);
    }

    public CompletableFuture<Void> addUrlAsync(List<URL> links) {
        return CompletableFuture.runAsync(() -> {
            this.addURL(links);
        });
    }

    public boolean isEmpty() {
        return this.urls.isEmpty();
    }


    @Override
    public JSONObject generateJson() {
        JSONObject repositoryJsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonArray.addAll(urls.stream().map(URL::toString).toList());
        repositoryJsonObject.put("urls", jsonArray);

        return repositoryJsonObject;
    }


    @Override
    public void init(JSONObject jsonObject) throws IOException, ParseException {

        JSONArray jsonArray = (JSONArray) jsonObject.get("urls");

        List<String> strList = jsonArray.stream().toList();

        strList.forEach(u -> {
            try {
                urls.add(new URL(u));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
        uniquenessChecker.start();
    }

    @Override
    public boolean clearData() {
        return clearData(path);
    }

    @Override
    public boolean stop() {
        uniquenessChecker.stop();
        return stop(path);
    }

    @Override
    public boolean start() {
        uniquenessChecker.start();
        return start(path);
    }

    @Override
    public boolean checkStart() {
        return checkStart(path);
    }
}
