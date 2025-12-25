package com.km.docmacc.intraoralbill.model.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class IntraOralData extends AmountDateCreated {
  private String category;
  private String procedureDone;
  private String toothNumbers;
}
