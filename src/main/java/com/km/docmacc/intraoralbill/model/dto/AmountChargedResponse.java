package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AmountChargedResponse extends AmountDateCreated {
  private String chargedAmount;
  private String discount;
  private String note;
}
