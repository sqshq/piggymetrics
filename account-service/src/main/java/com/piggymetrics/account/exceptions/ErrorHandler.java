package com.piggymetrics.account.exceptions;

import com.piggymetrics.account.exceptions.personalized.HttpErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.piggymetrics.account.application.util.Utility.printErrorMessage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(BAD_REQUEST)
  public HttpErrorResponse processValidationError(IllegalArgumentException e) {
    printErrorMessage(e.getMessage());
    return HttpErrorResponse.builder().message(e.getMessage()).httpStatus(BAD_REQUEST).build();
  }
}
