import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UniquenessChecker {
    private final Set<String> checked = new HashSet<>();

    public boolean check(final @NotNull URL url) {
        return this.checked.add(url.getHost());
    }
}
