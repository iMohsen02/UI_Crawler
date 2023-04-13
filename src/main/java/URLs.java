import java.net.URL;
import java.util.ArrayDeque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class URLs {
    private final Queue<URL> urls;

    {
        urls = new ArrayDeque<>();
    }

    public URLs(URL seed) {
        this.urls.add(seed);
    }

    public URL getNextURL() {
        return this.urls.remove();
    }

    public void addURL(URL...URLs) {
        this.urls.addAll(List.of(URLs));
    }
}
