package com.km.docmacc.intraoralbill.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountChargedRequest {
  private Long profileId;
  private LocalDate dateOfProcedure;
  private String chargedAmount;
  private String discount;
  private String note;
  private String category;
  private String procedureDone;
  private Integer toothNumber;
  private String createdByName;
  private String createdById;
}
