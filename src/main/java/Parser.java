import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final static String ignore = "txt, pdf, tif, tiff, bmp, jpg, jpeg, gif, png, esp, docs, xlsx, doc, pptm, xls, xlsm".trim().replace(", ", "|");

    private static @Nullable URL apply(String linkTag) {
        try {
            return new URL(linkTag);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public List<URL> parse(final @NotNull WebPage page) throws ContentNotFetchYet {
        return page.getContent()
                .select("a[href~=.*(?<!" + ignore + ")$]")
                .stream()
                .map(link -> Parser.apply(link.attr("href")))
                .filter(Objects::nonNull)
                .toList();

    }
}
