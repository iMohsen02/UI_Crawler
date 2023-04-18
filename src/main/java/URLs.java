import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.SynchronousQueue;

public class URLs {
    private final Queue<URL> urls;
    private final UniquenessChecker uniquenessChecker;

    {
        urls = new ConcurrentLinkedDeque<>();
        uniquenessChecker = new UniquenessChecker();
    }

    public URLs(URL seed) {
        this.urls.add(seed);
    }

    public URL getNextURL() {
        //noinspection LoopConditionNotUpdatedInsideLoop,StatementWithEmptyBody
        while (this.urls.isEmpty()) ;
        return this.urls.remove();
    }

    public void addURL(List<URL> links) {

        this.urls.addAll(
                links.stream()
                        .filter(uniquenessChecker::check)
                        .toList()
        );
    }

    public CompletableFuture<Void> addUrlAsync(List<URL> links) {
        return CompletableFuture.supplyAsync(() -> {
            this.addURL(links);
            return null;
        });
    }

    public boolean isEmpty() {
        return this.urls.isEmpty();
    }

}
