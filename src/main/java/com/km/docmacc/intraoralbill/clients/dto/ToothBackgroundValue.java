package com.km.docmacc.intraoralbill.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToothBackgroundValue {
  private Long id;
  private Long profileId;
  private Integer toothNumber;
  private String firstRow;
  private String secondRow;
  private String thirdRow;
  private String forthRow;
}
