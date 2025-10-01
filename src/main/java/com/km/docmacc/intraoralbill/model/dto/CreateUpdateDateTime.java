package com.km.docmacc.intraoralbill.model.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateDateTime implements Serializable {
    private LocalDate createdDate;
    private ZonedDateTime createdDateTime;
    private String createdByName;
    private String createdById;
    private LocalDate updatedDate;
    private ZonedDateTime updatedDateTime;
    private String updatedByName;
    private String updatedById;
}
