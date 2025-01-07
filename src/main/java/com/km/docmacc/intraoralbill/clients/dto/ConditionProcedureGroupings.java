package com.km.docmacc.intraoralbill.clients.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionProcedureGroupings {
  private List<ConditionProcedureResponse> conditions;
  private List<ConditionProcedureResponse> restorations;
  private List<ConditionProcedureResponse> restorationsInlay;
  private List<ConditionProcedureResponse> restorationsOnlay;
  private List<ConditionProcedureResponse> restorationsFluoride;
  private List<ConditionProcedureResponse> prosthetics;
  private List<ConditionProcedureResponse> denture;
  private List<ConditionProcedureResponse> periodontal;
  private List<ConditionProcedureResponse> surgery;
  private ConditionProcedureSurfaceRemarksResponse conditionProcedureSurfaceRemarksResponse;
}
