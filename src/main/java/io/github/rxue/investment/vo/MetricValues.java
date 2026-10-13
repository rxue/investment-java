package io.github.rxue.investment.vo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetricValues {
    private final String securityId;
    private final Map<Metric,Comparable<?>> values;
    private MetricValues(Builder builder) {
        this.securityId = builder.securityId;
        this.values = Collections.unmodifiableMap(builder.values);
    }

    public Comparable<?> get(Metric metric) {
        return values.get(metric);
    }
    public static class Builder {
        private final String securityId;
        private final Map<Metric,Comparable<?>> values;
        public Builder(String securityId) {
            this.securityId = securityId;
            this.values = new HashMap<>();
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
