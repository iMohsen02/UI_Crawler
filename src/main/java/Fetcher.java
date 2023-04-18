import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;

public class Fetcher {
    private final UrlBlocker urlBlocker;

    public Fetcher(UrlBlocker urlBlocker) {
        this.urlBlocker = urlBlocker;
    }

    public WebPage fetch(@NotNull WebPage webPage) {
//        long time = System.currentTimeMillis();
        downloadPage(webPage);

//        System.out.print("\tfetch time:\t" + (System.currentTimeMillis() - time) / 1000.0);
        return webPage;
    }

    private void downloadPage(@NotNull WebPage webPage) {
        if (urlBlocker.isBlocked(webPage))
            webPage.setStatus(WebPage.Status.FILTERED);
        else
            try {
                webPage.setContent(
                        Jsoup.connect(webPage.getUrl().toString()).get()
                );
                webPage.setStatus(WebPage.Status.CRAWLED);
            } catch (HttpStatusException httpStatusException) {
                webPage.setStatus(WebPage.Status.HTTPS_REQUEST_PROBLEM);
            } catch (ConnectException connectException) {
                webPage.setStatus(WebPage.Status.CONNECT_EXCEPTION);
            } catch (MalformedURLException malformedURLException) {
                webPage.setStatus(WebPage.Status.MALFORMED_URL_EXCEPTION);
            } catch (SocketTimeoutException socketTimeoutException) {
                webPage.setStatus(WebPage.Status.SOCKET_TIME_OUT_EXCEPTION);
            } catch (SSLHandshakeException sslHandshakeException) {
                webPage.setStatus(WebPage.Status.SSL_HAND_SHAKE_EXCEPTION);
            } catch (IOException ioException) {
                webPage.setStatus(WebPage.Status.IO_EXCEPTION);
            } catch (Exception exception) {
                webPage.setStatus(WebPage.Status.OTHER_EXCEPTION);
//                System.out.println(exception.getClass().getSimpleName());
            }
    }

    public CompletableFuture<WebPage> fetchAsync(@NotNull WebPage webpage) {
        return CompletableFuture.supplyAsync(() -> this.fetch(webpage));
    }
}
