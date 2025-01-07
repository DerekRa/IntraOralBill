package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToothSurfaceValue {

  private Long id;
  private Long profileId;
  private Integer toothNumber;
  private String top;
  private String bottom;
  private String left;
  private String right;
  private String center;
}
