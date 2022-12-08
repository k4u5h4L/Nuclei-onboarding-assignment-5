package com.gonuclei.allcaughtup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubscriptionDoesNotExistException extends RuntimeException {

  public SubscriptionDoesNotExistException(String msg) {
    super(msg);
  }
}
