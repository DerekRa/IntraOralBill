package com.km.docmacc.intraoralbill.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.km.docmacc.intraoralbill.model.dto.CreateUpdateDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="intraoralTreatmentPlanConsumer")
public class IntraoralTreatmentPlanConsumer extends CreateUpdateDateTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "intraoralTreatmentPlanConsumer_id", nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Long id;
    private Long profileId;
    private LocalDate dateOfProcedure;
    private Integer toothNumber;
    private Boolean communicationSuccessSent;
    private String consumerStatus;
}
