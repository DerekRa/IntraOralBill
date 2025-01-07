package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DentalChartDesignResponse {
  private String kindsOfTeeth;
  private String teethPositionStatus;
  private Integer teethNumbering;
  private String teethImage;
  private String teethImageLink;
  private String teethArea;
  private ToothSurfaceValue toothSurfaceValue;
  private ToothBackgroundValue toothBackgroundValue;
}
