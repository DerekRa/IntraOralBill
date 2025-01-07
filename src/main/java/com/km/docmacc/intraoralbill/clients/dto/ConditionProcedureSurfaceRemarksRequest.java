package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionProcedureSurfaceRemarksRequest {
  private String restorationInlayOther;
  private String restorationInlayNote;
  private String restorationOnlayOther;
  private String restorationOnlayNote;
  private String prostheticsNote;
  private String removablePartialDentureNote;
  private String completeDentureNote;
  private String almostCompleteDentureNote;
  private String surgeryReason;
  private String toothSurfaceNote;
}
