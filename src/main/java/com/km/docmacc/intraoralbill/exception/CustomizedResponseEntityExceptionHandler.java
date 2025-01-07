package com.km.docmacc.intraoralbill.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.km.docmacc.intraoralbill.model.dto.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){

    return createHttpResponse(BAD_REQUEST, ex.getFieldError().getDefaultMessage());

  }

  private ResponseEntity<Object> createHttpResponse(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
        httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
  }

}
