package com.example.uicrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Repository {
    private static FileWriter writer;

    private static Repository repository;

    private final static String path = Crawler.resources + "pages.xml";

    static {
        try {
            init(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Repository() {

    }

    public static void init(String path) throws IOException {
        writer = new FileWriter(path, true);
    }

    public static Repository getInstance() {
        if (repository == null) repository = new Repository();
        return repository;
    }

    public void saveContent(String content) throws IOException {
        //noinspection SynchronizeOnNonFinalField
        synchronized (writer) {
            writer.write(content);
        }
    }

    public CompletableFuture<Void> saveContentAsync(WebPage page) {
        return CompletableFuture.runAsync(() -> {
                    try {
                        saveContent(page.generateFileTemplate());
                        page.setStatus(WebPage.Status.CRAWLED);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public void closeRepository() throws IOException {
        writer.close();
    }
}
