package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillBreakdown {
  private String category;
  private String procedureDone;
  private Integer toothNumber;
  private String amountCharged;
  private String discount;
  private String amountPaid;
  private String payment;
  private String balance;
}
