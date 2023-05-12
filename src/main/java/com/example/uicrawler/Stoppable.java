package com.example.uicrawler;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public interface Stoppable extends Saveable{

    boolean stop();

    default boolean stop(String path) {
        try {
            writeJson(path);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    boolean start();
    default boolean start(String path) {
        try {
            init(readJson(path));
        } catch (IOException | ParseException e) {
            return false;
        }
        return true;
    };

    default CompletableFuture<Void> stopAsync() { return CompletableFuture.runAsync(() -> {
        stop();
    }, Executors.newSingleThreadExecutor()); }

    default boolean checkStart(String path) {
        return new File(path).exists();
    }
    boolean checkStart();
}
