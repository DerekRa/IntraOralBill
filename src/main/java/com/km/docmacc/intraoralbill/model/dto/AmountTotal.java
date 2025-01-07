package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AmountTotal {
  private String totalBill;
  private String totalPayment;
  private String totalBalance;
}
