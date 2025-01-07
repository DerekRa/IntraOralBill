package com.km.docmacc.intraoralbill.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BillBreakdownResponse extends AmountTotal{
  private List<BillBreakdown> billBreakdowns;
}
