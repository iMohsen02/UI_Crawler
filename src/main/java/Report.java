import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Report {

    private final Map<WebPage.Status, Integer> reports;

    {
        reports = new ConcurrentHashMap<>();
    }

    public void addReport(WebPage.Status status) {
        reports.put(status, !reports.containsKey(status) ? 1 : reports.get(status) + 1);
    }



    @Override
    public String toString() {
        return reports.entrySet().stream()
                .map(entry -> entry.getKey() + ":\t" + entry.getValue())
                .collect(Collectors.joining("\n")) + "\nTotal:\t" + reports.size();
    }
}
