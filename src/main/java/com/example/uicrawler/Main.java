package com.example.uicrawler;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws IOException, ContentNotFetchYet, InterruptedException {

        long first = System.currentTimeMillis();
        UrlBlocker filtered = new UrlBlocker();
        filtered.addBlock(
                "https://www.facebook",
                "https://facebook",
                "https://telegram",
                "https://www.telegram",
                "https://instagram",
                "https://www.instagram",
                "https://twitter",
                "https://www.twitter",
                "https://t.me",
                "https://youtube.me",
                "https://www.youtube.me"
        );

        Crawler crawler = new Crawler(new URL("https://www.sharif.edu/"), 12000, filtered);
        CompletableFuture.runAsync(() -> {
            crawler.crawlAsync();
        });

        new Scanner(System.in).nextLine();
        crawler.stop();
        new Scanner(System.in).nextLine();
        System.out.println(crawler);
        //        System.out.println((System.currentTimeMillis() - first) / 1000.0);

    }
}
