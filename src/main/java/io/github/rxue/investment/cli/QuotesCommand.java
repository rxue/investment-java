package io.github.rxue.investment.cli;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    @Parameters(index = "1", paramLabel = "TICKER_SYMBOLS_OR_CSV", description = "Ticker symbols delimited by comma, e.g. AAPL,PFE, or the csv storing ticker symbols")
    private String tickerSymbolsOrCsv;

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
        Set<String> tickerSymbolSet = tickerSymbols();
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

    /**
     * @return ticker symbols given directly, or all the non-blank values of the csv file in case a csv file path is given
     */
    private Set<String> tickerSymbols() {
        if (!tickerSymbolsOrCsv.toLowerCase().endsWith(".csv")) {
            return Arrays.stream(tickerSymbolsOrCsv.split(","))
                    .collect(toSet());
        }
        try (CSVParser csvParser = CSVParser.parse(Path.of(tickerSymbolsOrCsv), StandardCharsets.UTF_8, CSVFormat.DEFAULT)) {
            return csvParser.stream()
                    .flatMap(CSVRecord::stream)
                    .map(String::trim)
                    .filter(tickerSymbol -> !tickerSymbol.isEmpty())
                    .collect(toSet());
        } catch (IOException e) {
            throw new ParameterException(spec.commandLine(), "Failed to read csv file " + tickerSymbolsOrCsv + ": " + e, e);
        }
    }
}
