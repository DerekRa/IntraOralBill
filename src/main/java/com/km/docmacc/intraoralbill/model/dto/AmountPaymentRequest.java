package com.km.docmacc.intraoralbill.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountPaymentRequest {
  private Long profileId;
  private LocalDate dateOfProcedure;
  private String paymentAmount;
  private String note;
  private String category;
  private String procedureDone;
  private String toothNumbers;
  private String createdByName;
  private String createdById;
}
