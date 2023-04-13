import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Crawler {
    private final Fetcher fetcher;
    private final Parser parser;
    private final UniquenessChecker uniquenessChecker;
    private final Repository repository;
    private final URLs urls;
    private final int maxPage;

    public Crawler(URL seed, int maxPage) throws IOException {
        this.fetcher = new Fetcher();
        this.parser = new Parser();
        this.uniquenessChecker = new UniquenessChecker();
        Repository.init("src/main/resources/pages.xml");
        this.repository = Repository.getInstance();
        this.urls = new URLs(seed);
        this.maxPage = maxPage;
    }

    public void crawl() throws IOException, ContentNotFetchYet {
        for (int i = 0; i < maxPage; i++) {
            WebPage webpage = new WebPage(urls.getNextURL());
            try {
                webpage = fetcher.fetch(webpage);
                System.out.println(webpage.getUrl());
            } catch (Exception exception) {
                System.out.println("can't fetch. => " );
                i--;
                continue;
            }
            List<URL> subUrls = parser.parse(webpage);
            subUrls.forEach(System.out::println);

            repository.saveContent(webpage.generateFileTemplate());

            subUrls
                    .stream()
                    .filter(uniquenessChecker::check)
                    .forEach(urls::addURL);


        }
        repository.closeRepository();
    }
}
