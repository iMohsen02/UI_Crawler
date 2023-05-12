package com.example.uicrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.uicrawler.MainTest.WiFiManager.*;

class MainTest {

    @Test
    public void test1() throws IOException {
        String url = "https://api.proxyscrape.com/v2/?request=displayproxies&protocol=http&timeout=10000&country=all&ssl=all&anonymity=all";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("164.90.167.128", 8080));
        URLConnection conn = new URL(url).openConnection(proxy);
        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    public class WiFiManager {
        public static void disconnectWiFi() throws IOException {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh interface set interface \"Wi-Fi\" admin=disable");
            builder.start();
        }

        public static void connectWiFi() throws IOException {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh interface set interface \"Wi-Fi\" admin=enable");
            builder.start();
        }

        public static void resetConfig() {
            try {
                String command = "ipconfig /release";
                Process process = Runtime.getRuntime().exec(command);
                System.out.println("Network connection reset. IP address has been changed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public static void myIp() {
            try {
                InetAddress ipAddress = InetAddress.getLocalHost();
                System.out.println("IP address: " + ipAddress.getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void test3() throws InterruptedException {



    }

    @Test
    public void test2() throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) list.add(i);

        list.stream().filter(i -> i < 10).forEach(System.out::println);
    }

    @Test
    public void testFetch() throws IOException {
        Connection connection = Jsoup.connect("https://ut.ac.ir/fa");
        Connection.Response response = connection.method(Connection.Method.GET).execute();
        System.out.println(response.header("Content-Type").substring(0, 9).equals("text/html"));
        // Fetch page content using Jsoup
        String html = connection.get().html();
        System.out.println(html);
    }
}