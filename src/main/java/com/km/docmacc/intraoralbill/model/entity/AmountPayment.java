package com.km.docmacc.intraoralbill.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.km.docmacc.intraoralbill.model.dto.IntraOralData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="amountPayment")
public class AmountPayment extends IntraOralData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "amountPayment_id", nullable = false, updatable = false)
  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  private Long id;
  private Long profileId;
  private LocalDate dateOfProcedure;
  private String paymentAmount;
  private String note;


}
