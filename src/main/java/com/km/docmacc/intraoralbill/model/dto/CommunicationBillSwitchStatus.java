package com.km.docmacc.intraoralbill.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationBillSwitchStatus {
    private Long intraoralBillConsumerId;
    private String communicationStatus;
}
