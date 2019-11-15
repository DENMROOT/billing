package com.dmakarov.config.advice;

import com.dmakarov.model.exception.BillingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ResponseExceptionHandler {

  /**
   * Handles Response Exception, including its message into the response and wrapping it
   * in Response entity, depending on error type error message would be logged.
   *
   * @param e service exception
   * @return response entity
   */
  @ExceptionHandler
  public ResponseEntity handleException(BillingException e) {

    return ResponseEntity
        .status(e.getHttpStatus())
        .body(e.getMessage());
  }

}
