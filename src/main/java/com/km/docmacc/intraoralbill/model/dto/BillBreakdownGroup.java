package com.km.docmacc.intraoralbill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillBreakdownGroup {
    private String category;
    private String procedureDone;
    private String toothNumbers;
    private String amountCharged;
    private String discount;
    private String amountPaid;
    private String payment;
    private String balance;
}
