package com.km.docmacc.intraoralbill.exception.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException{

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime timeStamp;
  private int httpStatusCode; // 200, 201, 400, 500
  private HttpStatus httpStatus;
  private String reason;
  private String message;
  public CustomException(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
    super(message);
    this.timeStamp = LocalDateTime.now();
    this.httpStatusCode = httpStatusCode;
    this.httpStatus = httpStatus;
    this.reason = reason;
    this.message = message;
  }
}
