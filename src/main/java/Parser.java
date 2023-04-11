import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class Parser {

    public List<WebPage> parse(final @NotNull WebPage page) {

        return Jsoup.parse(page.getContent())
                .select("a")
                .stream()
                .map(linkTag -> {
                    try {
                        return new WebPage(new URL(linkTag.attr("href")), page.getDepth());
                    } catch (UnknownHostException | MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }
}
