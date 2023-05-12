package com.example.uicrawler;

import org.jsoup.nodes.Document;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class WebPage {
    private final URL url;
    private String hostIp;
    private Document content;
    private final int depth;

    private Status status;

    public Status getStatus() {
        return this.status;
    }


    public enum Status {
        CRAWLED, UNCRAWLED, HTTPS_PROBLEM, MALFORMED, TIME_OUT, FILTERED, OTHER_EXCEPTION, CONNECT_EXCEPTION, SSL_EXCEPTION, VALIDATOR_EXCEPTION, UNKNOWN_HOST, IO_EXCEPTION;
    }

    public WebPage(URL url) {
        this(url, 0);
    }

    public WebPage(URL url, int depth) {
        this.url = url;
        this.depth = depth;
        this.status = Status.UNCRAWLED;
        try {
            this.hostIp = InetAddress.getByName(url.getHost()).getHostAddress();
        } catch (UnknownHostException unknownHostException) {
            this.status = Status.UNKNOWN_HOST;
        }
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
        if (this.status == Status.UNCRAWLED) throw new ContentNotFetchYet();
        return content;
    }

    public int getDepth() {
        return depth;
    }


    public void setStatus(Status status) {
        this.status = status;
    }

    public String generateFileTemplate() {

        return this.content == null ? "" :
                "<doc>\n" +
                        "<Url>" + this.url + "</Url>\n" +
                        content.getElementsByTag("html") + "\n" +
                        content.getElementsByTag("title") + "\n" +
                        "</pure>" + content.text() + "</pure>\n" +
                        "</doc>\n";
    }

    @Override
    public String toString() {
        return "SiteAddress{ " +
                "url = " + url +
                ", ip = " + hostIp +
//                ", content = " + content +
                ", depth = " + depth +
                " }";
    }
}
