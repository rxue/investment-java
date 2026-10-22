# Java SE
## `ArithmeticException` caused by `divide` operation in `BigDecimal`
NOTE that the `BigDecimal.divide(BigDecimal divisor)`, say without rounding mode nor scale might have "non-terminating decimal expansion". This is related to the real math problem

# Practical Tips
## 20261022 Concurrency issue on `LegacyConcurrentQuoteFetcher`
* It undermines the point of concurrent mode. Up to 10 threads each fetch a cookie and a crumb before fetching any quotes.
* It's the most likely to cause real failures. Bursts of crumb requests are what Yahoo tends to rate-limit (HTTP 429). One rejected crumb fails the whole batch, and because of issue 2 you can't tell which ticker hit it.
