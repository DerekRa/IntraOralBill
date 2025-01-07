package com.km.docmacc.intraoralbill.clients.decoder;

import com.km.docmacc.intraoralbill.exception.domain.CustomException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

  /**
   * @param s
   * @param response
   * @return
   */
  @Override
  public Exception decode(String s, Response response) {

    switch (response.status()) {
      case 400:
        // Handle 400 Bad Request
        throw new CustomException(404, HttpStatus.BAD_REQUEST, "Bad Request", "Bad Request Data");
      case 404:
        // Handle 404 Not Found
        throw new CustomException(404, HttpStatus.NOT_FOUND, "Not Found", "404 Not Found Page");
      case 500:
        // Handle 500 Internal Server Error
        throw new CustomException(500, HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "Internal Server Error - IO Exception");
      default:
        return new CustomException(response.status(),
            HttpStatus.INTERNAL_SERVER_ERROR,response.reason(), response.reason());
    }
  }
}
