import org.jsoup.nodes.Document;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class WebPage {
    private final URL url;
    private final String hostIp;
    private Document content;
    private final int depth;

    public WebPage(URL url) throws UnknownHostException {
        this(url, 0);
    }

    public WebPage(URL url, int depth) throws UnknownHostException {
        this.url = url;
        this.depth = depth;
        this.hostIp = InetAddress.getByName(url.getHost()).getHostAddress();
    }

    public String getHostIp() {
        return hostIp;
    }

    public URL getUrl() {
        return url;
    }

    public void setContent(Document content) {
        this.content = content;
    }

    public Document getContent() throws ContentNotFetchYet {
        if (this.content == null) throw new ContentNotFetchYet();
        return content;
    }

    public int getDepth() {
        return depth;
    }


    public String generateFileTemplate() {

        return "<doc>\n" +
                "<Url>" + this.url + "</Url>\n" +
                content.getElementsByTag("html") + "\n" +
                content.getElementsByTag("title") + "\n" +
                content.getElementsByTag("body") + "\n" +
                "</doc>\n";
    }

    @Override
    public String toString() {
        return "SiteAddress{ " +
                "url = " + url +
                ", ip = " + hostIp +
                ", content = " + content +
                ", depth = " + depth +
                " }";
    }
}
