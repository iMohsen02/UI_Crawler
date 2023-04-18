import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UrlBlocker {
    private final List<String> block;

    {
        this.block = new ArrayList<>();
    }

    public void addBlock(String...links) {
        Collections.addAll(block, links);
    }

    public boolean isBlocked(WebPage page) {
        return block.stream()
                .anyMatch(b -> page.getUrl().toString().startsWith(b));
    }
}
