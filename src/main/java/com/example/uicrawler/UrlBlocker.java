package com.example.uicrawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UrlBlocker implements Stoppable {
    private final Set<String> block;
    public static final String path = Crawler.resources + UrlBlocker.class.getName() + ".json";

    {
        this.block = new HashSet<>();
    }

    public void addBlock(String... links) {
        block.addAll( Arrays.stream(links).filter(link -> !link.trim().equals("")).toList());
    }

    public boolean isBlocked(WebPage page) {
        return block.stream()
                .anyMatch(b -> page.getUrl().toString().startsWith(b));
    }


    @Override
    public JSONObject generateJson() {
        JSONObject urlBlockerJson = new JSONObject();

        JSONArray blockListArrayJson = new JSONArray();
        blockListArrayJson.addAll(block);

        urlBlockerJson.put("block", blockListArrayJson);

        return urlBlockerJson;
    }

    @Override
    public void init(JSONObject jsonObject) throws IOException, ParseException {
        JSONArray jsonList = (JSONArray) jsonObject.get("block");
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
}
