package io.github.rxue.investment.vo;

import java.util.Comparator;
import java.util.List;

public record MetricValuesList(List<MetricValues> valuesList) {
    /**
     * @return values sorted ascending by the value of the given metric, missing (null) values are ordered last
     */
    @SuppressWarnings("unchecked")
    public List<MetricValues> sortedBy(Metric metric) {
        Comparator<MetricValues> byMetricValue = Comparator.comparing(
                metricValues -> (Comparable<Object>) metricValues.get(metric),
                Comparator.nullsLast(Comparator.naturalOrder()));
        return valuesList.stream().sorted(byMetricValue).toList();
    }
}
