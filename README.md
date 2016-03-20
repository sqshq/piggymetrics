[![Build Status](https://travis-ci.org/sqshq/PiggyMetrics.svg?branch=master)](https://travis-ci.org/sqshq/PiggyMetrics)
[![codecov.io](https://codecov.io/github/sqshq/PiggyMetrics/coverage.svg?branch=master)](https://codecov.io/github/sqshq/PiggyMetrics?branch=master)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/sqshq/PiggyMetrics/blob/master/LICENCE)
[![Join the chat at https://gitter.im/sqshq/PiggyMetrics](https://badges.gitter.im/sqshq/PiggyMetrics.svg)](https://gitter.im/sqshq/PiggyMetrics?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Piggy Metrics

**A simple way to deal with personal finances**

This is a [proof-of-concept application](http://my-piggymetrics.rhcloud.com), which demonstrates [Microservice Architecture Pattern](http://martinfowler.com/microservices/) using Spring Boot, Spring Cloud and Docker.
With a pretty neat user interface, by the way.

![](https://cloud.githubusercontent.com/assets/6069066/13864234/442d6faa-ecb9-11e5-9929-34a9539acde0.png)
![Piggy Metrics](https://cloud.githubusercontent.com/assets/6069066/13830155/572e7552-ebe4-11e5-918f-637a49dff9a2.gif)

## Functional services

PiggyMetrics was decomposed into three core microservices. All of them are independently deployable applications, organized around certain business capability.

<img width="880" alt="Functional services" src="https://cloud.githubusercontent.com/assets/6069066/13900465/730f2922-ee20-11e5-8df0-e7b51c668847.png">

#### Account service
Contains general user input logic and validation: incomes/expenses items, savings and account settings.

#### Statistics service
Performs calculations on major statistics parameters and captures time series for each account. Datapoint contains values, normalized to base currency and time period. This data is used to track incomes/expenses dynamics in account lifetime (charts not yet implemented in UI)

#### Notification service
Stores users contact information and notification settings (like remind and backup frequency). Scheduled worker collects required information from other services and sends e-mail messages to subscribed customers.

#### N.B.
- Each microservice has it's own database, so there is no way to bypass API and access persistance data directly.
- In this project, I use Mongodb as a primary database for each service. It might also make sense to have a polyglot persistence architecture (сhoose the type of db, that is best suited to service requirements).
- Another simplification in this project is service-to-service communication: services talking only by synchronous rest API. It's a good practice to use combination of interaction styles in a real-world systems. For example, perform synchronous GET request to retrieve data and use asynchronous approach via some Message broker for create/update operations.

## Infrastructure services
<img width="880" alt="Infrastructure services" src="https://cloud.githubusercontent.com/assets/6069066/13905633/dcbcd838-eede-11e5-9502-38a378a38d54.png">

## Support

PiggyMetrics is open source, and would greatly appreciate your help. Feel free to contact me with any questions.
