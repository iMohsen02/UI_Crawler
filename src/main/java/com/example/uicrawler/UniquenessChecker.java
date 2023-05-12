package com.example.uicrawler;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class UniquenessChecker implements Stoppable {
    private final Set<String> checked = new ConcurrentSkipListSet<>();
    public static final String path = Crawler.resources + UniquenessChecker.class.getName() + ".json";

    public boolean check(final @NotNull URL url) {
        if (checked.contains(url.toString())) {
            return false;
        }

        checked.add(url.toString());
        return true;
    }


    @Override
    public JSONObject generateJson() {
        JSONObject uniquenessCheckerJson = new JSONObject();
        JSONArray array = new JSONArray();
        //noinspection unchecked
//        array.addAll(checked);
        //noinspection unchecked
        uniquenessCheckerJson.put("checked", array);
        return uniquenessCheckerJson;
    }

    @Override
    public void init(JSONObject jsonObject) throws IOException, ParseException {
        JSONArray dataArray = (JSONArray) jsonObject.get("checked");
        dataArray.forEach(c -> this.checked.add(c.toString()));
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
