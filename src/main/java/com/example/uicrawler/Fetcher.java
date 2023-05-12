package com.example.uicrawler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Fetcher {
    private final UrlBlocker urlBlocker;
    private Queue<Proxy> proxies = new ConcurrentLinkedDeque<>();

    public Fetcher(UrlBlocker urlBlocker) {
        this.urlBlocker = urlBlocker;
    }

    public WebPage fetch(@NotNull WebPage webPage) {
        downloadPage(webPage);

        return webPage;
    }

    private void downloadPage(@NotNull WebPage webPage) {
        if (urlBlocker.isBlocked(webPage))
            webPage.setStatus(WebPage.Status.FILTERED);
        else
            try {

                Connection connect = checkHeader(webPage);
                if (connect == null) return;

                webPage.setContent(connect.get().normalise());
                webPage.setStatus(WebPage.Status.CRAWLED);

            } catch (HttpStatusException httpStatusException) {
                webPage.setStatus(WebPage.Status.HTTPS_PROBLEM);
            } catch (ConnectException connectException) {
                webPage.setStatus(WebPage.Status.CONNECT_EXCEPTION);
            } catch (MalformedURLException malformedURLException) {
                webPage.setStatus(WebPage.Status.MALFORMED);
            } catch (SocketTimeoutException socketTimeoutException) {
                webPage.setStatus(WebPage.Status.TIME_OUT);
            } catch (SSLHandshakeException sslHandshakeException) {
                webPage.setStatus(WebPage.Status.SSL_EXCEPTION);
            } catch (IOException ioException) {
                webPage.setStatus(WebPage.Status.IO_EXCEPTION);
            } catch (Exception exception) {
                webPage.setStatus(WebPage.Status.OTHER_EXCEPTION);
            }
    }

    @Nullable
    private Connection checkHeader(@NotNull WebPage webPage) throws IOException {
        Connection connect = Jsoup.connect(webPage.getUrl().toString());
        Connection.Response response = connect.method(Connection.Method.GET).execute();
        // todo add html status to state
        if (!Objects.requireNonNull(response.header("Content-Type")).startsWith("text/html")) {
            webPage.setStatus(WebPage.Status.FILTERED);
            return null;
        }
        return connect;
    }

    public CompletableFuture<WebPage> fetchAsync(@NotNull WebPage webpage) {
        return CompletableFuture.supplyAsync(() -> this.fetch(webpage));
    }
}
