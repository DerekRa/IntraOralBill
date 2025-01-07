package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest extends AmountData{

  private Integer pageNo;
  private Integer pageSize;
  private String sortBy;
  private String orderBy;
  private String findItem;
}
