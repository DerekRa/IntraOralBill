package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionProcedureRequest {
  private Boolean checked;
  private String group;
  private String label;
  private String name;
  private String value;
  private String formControlName;
  private String inputId;
}
