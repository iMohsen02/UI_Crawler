import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Crawler {
    private final Fetcher fetcher;
    private final Parser parser;
    private final Repository repository;
    private final URLs urls;
    private final int maxPage;
    private final Report report;

    public Crawler(URL seed, int maxPage, UrlBlocker urlBlocker) throws IOException {
        this.fetcher = new Fetcher(urlBlocker);
        this.parser = new Parser();
        Repository.init("src/main/resources/pages.xml");
        this.repository = Repository.getInstance();
        this.urls = new URLs(seed);
        this.maxPage = maxPage;
        this.report = new Report();
    }

    public void crawl() throws ContentNotFetchYet {

        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
        for (int i = 0; i < maxPage; i++) {
            System.out.println(i);
            final WebPage webpage = new WebPage(urls.getNextURL());

            completableFutureList.add(
                    fetcher.fetchAsync(webpage)
                            .thenComposeAsync(parser::parseAsync)
                            .thenAcceptAsync(urls::addUrlAsync)
                            .thenRunAsync(() -> {
                                try {
                                    repository.saveContent(webpage.generateFileTemplate());
                                } catch (IOException e) {
                                    System.out.println("Crawler can't continue because IO exception accrued. can't write content");
                                    throw new RuntimeException(e);
                                }
                            }).thenRunAsync(() -> report.addReport(webpage.getStatus()))
            );


        }
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).thenRun(() -> {
            try {
                repository.closeRepository();
                System.out.println(report);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
