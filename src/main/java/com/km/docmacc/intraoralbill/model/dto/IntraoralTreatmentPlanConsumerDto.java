package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntraoralTreatmentPlanConsumerDto {
    private Long consumerId;
    private Long profileId;
    private Integer toothNumber;
    private LocalDate dateOfProcedure;
    private AmountTotal amountTotal;
}
