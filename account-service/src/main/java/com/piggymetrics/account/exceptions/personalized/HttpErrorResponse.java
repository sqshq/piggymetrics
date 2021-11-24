package com.piggymetrics.account.exceptions.personalized;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
/** @author Jhooomn */
@Builder
public class HttpErrorResponse implements Serializable {
  private HttpStatus httpStatus;
  private String message;
}
