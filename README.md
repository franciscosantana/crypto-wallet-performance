# Crypto wallet performance

Given a CSV file as a crypto wallet, this app analyzes it against the current positions of your assets. So that indicates its total value and asset with best and worst performance.

## Installing / Getting started

This is a case study using Java multithreading features and practices. In this way, the use of third-party framework or libs was avoided.

### Environment setup
To package and run this project use `Maven 3.6` and `Java 11+`.

### External API setup
CoinCap API (https://docs.coincap.io/) is used to retrieve current asset prices. An access key is necessary to use this API.

1. Generate API key on https://coincap.io/api-key.
2. Fill the API key on `application.properties`.

> Note: When in production environment, the use of profiles or secret vaults to maintain the API key instead of keeping it in the code is encouraged.
