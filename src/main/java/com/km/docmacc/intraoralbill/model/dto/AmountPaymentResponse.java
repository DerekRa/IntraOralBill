package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountPaymentResponse extends AmountDateCreated {

  private String paymentAmount;
  private String note;
}
