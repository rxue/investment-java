package io.github.rxue.investment.cli;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.QuotePrice;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "quotes", description = "Prints quote metrics of a security", mixinStandardHelpOptions = true)
public class QuotesCommand implements Callable<Integer> {
    private final Repository repository;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", arity = "1", paramLabel = "METRICS", split = ",",
            description = "Comma separated metric names. Valid values: ${COMPLETION-CANDIDATES}")
    private Set<QuoteMetric> metrics;

    @Parameters(index = "1", paramLabel = "SYMBOL", description = "Ticker symbol, e.g. AAPL")
    private String symbol;

    public QuotesCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() {
        Map<QuoteMetric, Comparable<?>> values = repository.findMetricValues(symbol, metrics);
        PrintWriter out = spec.commandLine().getOut();
        for (QuoteMetric metric : metrics) {
            out.println(metric.label() + ": " + format(values.get(metric)));
        }
        return 0;
    }

    private static String format(Comparable<?> value) {
        if (value instanceof QuotePrice price) {
            return price.value() + " " + price.currency();
        }
        return String.valueOf(value);
    }
}
