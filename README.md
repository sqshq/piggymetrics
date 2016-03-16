[![Build Status](https://travis-ci.org/sqshq/PiggyMetrics.svg?branch=master)](https://travis-ci.org/sqshq/PiggyMetrics)
[![codecov.io](https://codecov.io/github/sqshq/PiggyMetrics/coverage.svg?branch=master)](https://codecov.io/github/sqshq/PiggyMetrics?branch=master)
[![Join the chat at https://gitter.im/sqshq/PiggyMetrics](https://badges.gitter.im/sqshq/PiggyMetrics.svg)](https://gitter.im/sqshq/PiggyMetrics?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Piggy Metrics

**A Simple way to deal with personal finances**

This is [a small web application](http://my-piggymetrics.rhcloud.com) which could help you to estimate regular expenses and analyze your own financial statistics.

![Piggy Metrics](https://cloud.githubusercontent.com/assets/6069066/13830155/572e7552-ebe4-11e5-918f-637a49dff9a2.gif)

## Structure

Spring MVC based backend with the following functions:

- sign up / login users
- multi-language Demo account
- saving user's data
- parsing current quotes from The Central Bank of Russia XML
- multi-language autodetect (english/russian packs included)
- regularly automatic backup to email

js/jQuery frontend:

- ajax interactions with server
- incomes, expenses, savings and notes input
- live statistics/forecasts computation

## Run in Docker container
- `make build`
- `make run`
- hit `http://localhost:8080`

## Notes
- database password is provided in the [Makefile](Makefile), application gets it from environment variable. You can specify jdbc connection stuff in [dev.properties](/src/main/resources/config/dev.properties) file
- [dev.log4j.xml](/src/main/resources/config/dev.log4j.xml) and [dev.properties](/src/main/resources/config/dev.properties) files may contain email credentials for log email reports and backup functioning

## Support

PiggyMetrics is open source, and would greatly appreciate your help. Feel free to contact me with any questions, especially if you found some bugs or issues

## License

This project is licensed under the MIT License.
