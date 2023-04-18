import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Repository {
    private static FileWriter writer;

    private static Repository repository;

    private static final AtomicInteger writtenPage;

    static {
        try {
            init("src/main/resources/pages.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writtenPage = new AtomicInteger(0);
    }

    private Repository() {

    }

    public static void init(String path) throws IOException {
        writer = new FileWriter(path, true);
    }

    public static Repository getInstance() {
        if (repository == null) repository = new Repository();
        return repository;
    }

    public synchronized void saveContent(String content) throws IOException {
        writer.write(content);
        writtenPage.incrementAndGet();
    }

    public void closeRepository() throws IOException {
        writer.close();
    }
}
