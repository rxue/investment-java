package io.github.rxue.investment.vo;

import java.util.Map;

public record MetricValues(String securityId, Map<Metric,Comparable<?>> values) {
    public Comparable<?> get(Metric metric) {
        return values.get(metric);
    }
}
