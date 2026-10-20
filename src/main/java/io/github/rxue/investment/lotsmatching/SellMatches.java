package io.github.rxue.investment.lotsmatching;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public record SellMatches(Map<String,List<SellMatch>> matchesBySecurity) {
    public SellMatches since(LocalDate inclusiveStartDate) {
        Map<String,List<SellMatch>> filteredResult = matchesBySecurity.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> since(e.getValue(), inclusiveStartDate)));
        return new SellMatches(filteredResult);
    }
    private static List<SellMatch> since(List<SellMatch> matches, LocalDate inclusiveStartDate) {
        return matches.stream()
                .filter(m -> !m.sellLot().date().isBefore(inclusiveStartDate))
                .toList();
    }
    public long totalGainInCent() {
        return matchesBySecurity.values().stream()
                .flatMap(List::stream)
                .mapToLong(SellMatch::gainInCent)
                .sum();
    }
}