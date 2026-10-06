package io.github.rxue.investment.vo;

import java.util.Map;

public record MetricValues(Map<Metric,Comparable<?>> values) {
}
