package com.km.docmacc.intraoralbill.clients.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IntraoralExaminationResponse extends IntraoralExaminationResponseDto{
  private DentalChartDesignResponse dentalChartDesignResponse;
  private ConditionProcedureGroupings conditionProcedureGroupings;
  private List<SurfaceCheckResponse> surfaceCheckResponses;
}
