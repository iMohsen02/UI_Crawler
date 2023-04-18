import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

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

        Crawler crawler = new Crawler(new URL("https://yazd.ac.ir/"), 12000, filtered);
        crawler.crawl();

        new Scanner(System.in).nextLine();
//        System.out.println((System.currentTimeMillis() - first) / 1000.0);

    }
}
