package com.km.docmacc.intraoralbill.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class AmountDateCreated {

  @JsonFormat(pattern="yyyy-MM-dd")
  private LocalDate createdDate;
  private LocalDateTime createdDateTime;
  private String createdByName;
  private String createdById;

}
