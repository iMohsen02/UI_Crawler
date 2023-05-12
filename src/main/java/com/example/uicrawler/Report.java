package com.example.uicrawler;

import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Report implements Stoppable {

    private final Map<WebPage.Status, Integer> reports;
    public static final String path = Crawler.resources + Report.class.getName() + ".json";

    {
        reports = new ConcurrentHashMap<>();
    }

    public void addReport(WebPage.Status status) {
        reports.put(status, !reports.containsKey(status) ? 1 : reports.get(status) + 1);
    }

    public CompletableFuture<Void> addReportAsync(WebPage.Status status) {
        return CompletableFuture.runAsync(() -> addReport(status));
    }

    @Override
    public String toString() {
        return Arrays.stream(WebPage.Status.values())
                .map(en -> en.name().replace("_", " ").toLowerCase() + ":\t" + reports.getOrDefault(en, 0))
                .collect(Collectors.joining("\n")) + "\nTotal:\t" + getNumberOfAllTried();
    }

    public int getNumberOfAllTried() {
        return reports.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getCrawledPageNum() {
        return reports.getOrDefault(WebPage.Status.CRAWLED, 0);
    }


    @Override
    public JSONObject generateJson() {
        JSONObject reportJson = new JSONObject();

        Arrays.stream(WebPage.Status.values())
                .forEach(state -> reportJson.put(state.toString(), reports.getOrDefault(state, 0)));
        return reportJson;
    }

    @Override
    public void init(JSONObject jsonObject) {
        Arrays.stream(WebPage.Status.values())
                .forEach(status -> reports.put(status, Math.toIntExact((Long) jsonObject.get(status.toString()))));
    }

    @Override
    public boolean clearData() {
        return clearData(path);
    }

    @Override
    public boolean stop() {
        return stop(path);
    }

    @Override
    public boolean start() {
        return start(path);
    }

    @Override
    public boolean checkStart() {
        return checkStart(path);
    }

    public int getNumOf(String name) {
        return reports.getOrDefault(WebPage.Status.valueOf(name), 0);
    }
}
