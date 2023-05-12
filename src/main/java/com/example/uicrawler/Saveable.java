package com.example.uicrawler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Saveable {

    default void writeJson(String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(generateJson().toJSONString());
        writer.close();
    }

    default JSONObject readJson(String path) throws IOException, ParseException {
        if (!Files.exists(Path.of(path))) throw new FileNotFoundException();
        FileReader reader = new FileReader(path);
        return (JSONObject) new JSONParser().parse(reader);
    }

    JSONObject generateJson();

    void init(JSONObject jsonObject) throws IOException, ParseException;

    default boolean clearData(String path) {
        return new File(path).delete();
    }

    boolean clearData();
}
