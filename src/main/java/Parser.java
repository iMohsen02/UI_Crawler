import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Parser {
    private final static String ignore = "txt, pdf, tif, tiff, bmp, jpg, jpeg, gif, png, esp, docs, xlsx, doc, pptm, xls, xlsm".trim().replace(", ", "|");

    private static @Nullable URL apply(String linkTag) {
        try {
            return new URL(linkTag);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public List<URL> parse(final @NotNull WebPage page) {

//        long time = System.currentTimeMillis();
        List<URL> hrefs = page.getStatus() != WebPage.Status.CRAWLED ? new ArrayList<>() :
                page.getContent()
                        .select("a[href~=.*(?<!" + ignore + ")$]")
                        .stream()
                        .map(link -> Parser.apply(link.attr("href")))
                        .filter(Objects::nonNull)
                        .toList();

//        System.out.println("\tparse:\t" + (System.currentTimeMillis() - time) / 1000.);
//        System.out.println("\t\t" + page);
        return hrefs;
    }

    public CompletableFuture<List<URL>> parseAsync(final @NotNull WebPage page) {
        return CompletableFuture.supplyAsync(() -> this.parse(page));
    }
}
