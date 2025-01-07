package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionProcedureResponse extends ConditionProcedureRequest {
  private Long id;
}
