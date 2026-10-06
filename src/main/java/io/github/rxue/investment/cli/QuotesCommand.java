package io.github.rxue.investment.cli;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Command(name = "quotes", description = "Prints quote metrics of a security", mixinStandardHelpOptions = true)
public class QuotesCommand implements Callable<Integer> {
    private final Repository repository;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", arity = "1", paramLabel = "METRICS", split = ",",
            description = "Comma separated metric names. Valid values: ${COMPLETION-CANDIDATES}")
    private Set<QuoteMetric> metrics;

    @Parameters(index = "1", paramLabel = "TICKER_SYMBOLS", description = "Ticker symbols delimited by comma, e.g. AAPL,PFE")
    private String tickerSymbols;

    @Option(names = "--sort-by", paramLabel = "METRIC",
            description = "Metric to sort the result ascending by, must be one of METRICS")
    private QuoteMetric sortBy;

    public QuotesCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() {
        if (sortBy != null && !metrics.contains(sortBy)) {
            throw new ParameterException(spec.commandLine(), "--sort-by metric " + sortBy + " must be one of METRICS");
        }
        Set<String> tickerSymbolSet = Arrays.stream(tickerSymbols.split(","))
                .collect(toSet());
        long findStartNanos = System.nanoTime();
        MetricValuesList valuesList = repository.findMetricValues(tickerSymbolSet, metrics);
        double findSeconds = (System.nanoTime() - findStartNanos) / 1_000_000_000.0;
        List<MetricValues> sortedValuesList = sortBy == null ? valuesList.valuesList() : valuesList.sortedBy(sortBy);
        List<String> labels = metrics.stream().map(QuoteMetric::label).toList();
        List<List<String>> rows = sortedValuesList.stream()
                .map(metricValues -> metrics.stream().map(metric -> "" + metricValues.get(metric)).toList())
                .toList();
        String rowFormat = IntStream.range(0, labels.size())
                .mapToObj(i -> "%-" + Math.max(labels.get(i).length(),
                        rows.stream().mapToInt(row -> row.get(i).length()).max().orElse(0)) + "s")
                .collect(Collectors.joining("  ")) + "%n";
        PrintWriter out = spec.commandLine().getOut();
        out.printf(rowFormat, labels.toArray());
        rows.forEach(row -> out.printf(rowFormat, row.toArray()));
        out.printf("Fetched in %.3f seconds%n", findSeconds);
        return 0;
    }
}
