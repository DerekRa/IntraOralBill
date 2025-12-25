package com.km.docmacc.intraoralbill.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AmountData {
  private Long profileId;
  private LocalDate dateOfProcedure;
  private String category;
  private String procedureDone;
  private String toothNumbers;
}
