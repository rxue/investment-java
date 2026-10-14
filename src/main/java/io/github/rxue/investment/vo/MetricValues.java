package io.github.rxue.investment.vo;

import java.util.*;

public class MetricValues {
    private final String securityId;
    private final SequencedMap<Metric,Comparable<?>> values;
    private MetricValues(Builder builder) {
        this.securityId = builder.securityId;
        this.values = Collections.unmodifiableSequencedMap(builder.values);
    }
    public String securityId() {
        return securityId;
    }
    public List<Metric> metricList() {
        return values.keySet().stream()
                .toList();
    }

    public Comparable<?> get(Metric metric) {
        return values.get(metric);
    }
    public static class Builder {
        private final String securityId;
        private final SequencedMap<Metric,Comparable<?>> values;
        public Builder(String securityId) {
            this.securityId = securityId;
            this.values = new LinkedHashMap<>();
        }
        public Builder add(Metric metric, Comparable<?> value) {
            values.put(metric, value);
            return this;
        }
        public Builder addAll(Map<? extends Metric,? extends Comparable<?>> values) {
            this.values.putAll(values);
            return this;
        }
        public MetricValues build() {
            return new MetricValues(this);
        }
    }
}
