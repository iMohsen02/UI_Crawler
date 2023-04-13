import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class Crawler {
    private Fetcher fetcher;
    private Parser parser;
    private UniquenessChecker uniquenessChecker;
    private Repository repository;
    private URLs urls;
    private int maxPage;

    public Crawler(URL seed, int maxPage) throws IOException {
        this.fetcher = new Fetcher();
        this.parser = new Parser();
        this.uniquenessChecker = new UniquenessChecker();
        Repository.init("src/main/resources/pages.txt");
        this.repository = Repository.getInstance();
        this.urls = new URLs(seed);
        this.maxPage = maxPage;
    }

    public void crawl() throws IOException {
        for (int i = 0; i < maxPage; i++) {
            WebPage webpage;
            try {
                webpage = fetcher.fetch(new WebPage(urls.getNextURL()));
            } catch (Exception exception) {
                System.out.println("can't fetch.");
                i--;
                continue;
            }
            List<URL> subUrls = parser.parse(webpage);
            repository.saveContent(webpage.getContent());

            subUrls
                    .stream()
                    .filter(uniquenessChecker::check)
                    .forEach(urls::addURL);


        }
    }
}
