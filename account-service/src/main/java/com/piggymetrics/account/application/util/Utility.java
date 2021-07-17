package com.piggymetrics.account.application.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Utility {
  public static void printErrorMessage(final String errorMessage) {
    log.error(errorMessage);
  }
}
