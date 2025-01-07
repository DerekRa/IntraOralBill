package com.km.docmacc.intraoralbill.clients.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IntraoralExaminationDto {
  private Long profileId;
  private Long dentalChartDesignId;
  private LocalDate dateOfProcedure;
}
