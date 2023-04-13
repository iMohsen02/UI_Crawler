import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException, ContentNotFetchYet {

        Crawler crawler = new Crawler(new URL("https://yazd.ac.ir/"), 2);
        crawler.crawl();

    }
}
