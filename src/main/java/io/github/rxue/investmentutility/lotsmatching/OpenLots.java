package io.github.rxue.investmentutility.lotsmatching;

import java.util.List;

public class OpenLots {
    private final String securityId;
    private final List<Lot.Buy> lots;
    OpenLots(String securityId, List<Lot.Buy> lots) {
        this.securityId = securityId;
        this.lots = lots;
    }
    public String securityId() {
        return securityId;
    }
    public long costInCent() {
        return lots.stream()
                .mapToLong(Lot.Buy::valueInCent)
                .sum();
    }
    public int shareAmount() {
        return lots.stream()
                .mapToInt(Lot.Buy::shareAmount)
                .sum();
    }
}
