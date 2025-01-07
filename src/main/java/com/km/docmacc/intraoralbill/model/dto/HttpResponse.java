package com.km.docmacc.intraoralbill.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

  /*@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/New_York")*/
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime timeStamp;
  private int httpStatusCode; // 200, 201, 400, 500
  private HttpStatus httpStatus;
  private String reason;
  private String message;
  public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
    this.timeStamp = LocalDateTime.now();
    this.httpStatusCode = httpStatusCode;
    this.httpStatus = httpStatus;
    this.reason = reason;
    this.message = message;
  }
}
