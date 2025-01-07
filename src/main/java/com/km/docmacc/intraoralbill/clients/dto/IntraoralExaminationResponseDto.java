package com.km.docmacc.intraoralbill.clients.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IntraoralExaminationResponseDto extends IntraoralExaminationDto{
  private Long id;
  private ZonedDateTime createdAt;
  private Long createdBy;
  private ZonedDateTime updatedAt;
  private Long updatedBy;
}
