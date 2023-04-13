import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Fetcher {
    public WebPage fetch(@NotNull WebPage webPage) throws IOException {

        webPage.setContent(
                Jsoup.connect(webPage.getUrl().toString()).get()
        );
        return webPage;
    }
}
