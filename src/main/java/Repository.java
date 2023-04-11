import java.io.FileWriter;
import java.io.IOException;

public class Repository {
    private static FileWriter writer;

    private static Repository repository;

    private Repository() {
    }

    public static void init(String path) throws IOException {
        writer = new FileWriter(path);
    }

    public static Repository getInstance() {
        if (repository == null) repository = new Repository();
        return repository;
    }

    public void saveContent(String content) throws IOException {
        writer.write(content);
    }

    public void closeRepository() throws IOException {
        writer.close();
    }
}
