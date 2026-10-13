package io.github.rxue.investment.vo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record MetricValuesList(Map<String,MetricValues> valuesBySecurityId) {
    /**
     * @return values sorted ascending by the value of the given metric, missing (null) values are ordered last
     */
    @SuppressWarnings("unchecked")
    public List<MetricValues> sortedBy(Metric metric) {
        Comparator<MetricValues> byMetricValue = Comparator.comparing(
                metricValues -> (Comparable<Object>) metricValues.get(metric),
                Comparator.nullsLast(Comparator.naturalOrder()));
        return valuesBySecurityId.values().stream().sorted(byMetricValue).toList();
    }
    public List<MetricValues> valuesList() {
        return valuesBySecurityId.values().stream()
                .toList();
    }
    public <T> T getMetricValue(String securityId, Metric metric, Class<T> valueType) {
        Comparable<?> comparableValue = valuesBySecurityId.get(securityId)
                .get(metric);
        return valueType.cast(comparableValue);
    }
}
