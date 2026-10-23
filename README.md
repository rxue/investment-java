# investment-utility

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Introduction
`investment-utility` is a Java 21 library and command line tool for personal investment bookkeeping and analysis.

It currently provides:
* **Market quotes** – fetches quote metrics of securities, such as company name, latest market price, trailing P/E and dividend yield, from Yahoo Finance. The latest market price can also be converted to a configured report currency.
* **FX rates** – fetches foreign exchange rates published by the European Central Bank (ECB).
* **Lots matching** – matches the sell trades of a security against its buy trades in FIFO order.
* **Command line interface** – the `quotes` command prints the quote metrics of the given ticker symbols as a table, optionally sorted by one of the metrics.

## Example
Print the name, latest market price, trailing P/E and dividend yield of Apple and Pfizer, sorted by trailing P/E:
```bash
mvn -q compile exec:java \
  -Dexec.mainClass=io.github.rxue.investmentutility.cli.Main \
  -Dexec.args="quotes COMPANY_NAME,LATEST_MARKET_PRICE,TRAILING_PE,DIVIDEND_YIELD AAPL,PFE --sort-by TRAILING_PE"
```
Output:
```
Name         Latest market price  Trailing P/E  Dividend yield
Pfizer Inc.  28.34 USD            37.28         6.06%
Apple Inc.   342.14 USD           39.24         0.32%
Fetched in 0.943 seconds
```
The ticker symbols can also be given as a path to a csv file, e.g. `tickers.csv`.

### Concurrent fetching
By default the ticker symbols are fetched one after another. With the `--concurrent` option they are fetched in parallel by a pool of 10 threads sharing one Yahoo Finance session, which is considerably faster for a long list of ticker symbols, e.g. from a csv file:
```bash
mvn -q compile exec:java \
  -Dexec.mainClass=io.github.rxue.investmentutility.cli.Main \
  -Dexec.args="quotes COMPANY_NAME,LATEST_MARKET_PRICE tickers.csv --concurrent"
```
