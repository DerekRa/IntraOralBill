package com.km.docmacc.intraoralbill.repository;

import com.km.docmacc.intraoralbill.model.entity.AmountCharged;
import com.km.docmacc.intraoralbill.model.entity.AmountPayment;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends
    JpaRepository<AmountPayment, Long> {

  List<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndPaymentAmountLike(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone, String paymentAmount, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndNoteLike(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone, String note, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedByNameLike(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone, String createdByName, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedDate(Long profileId, LocalDate dateOfProcedure, String category, String toothNumbers, String procedureDone, LocalDate createdDate, Pageable paging);
  @Query("FROM AmountPayment i WHERE i.profileId = :profileId AND i.dateOfProcedure = :dateOfProcedure AND i.category = :category AND i.toothNumbers = :toothNumbers AND i.procedureDone = :procedureDone AND " +
      "(i.paymentAmount like :columnValue OR i.note like :columnValue OR i.createdByName like :columnValue)")
  Page<AmountPayment> findPatientAmountPayment(@Param("profileId") Long profileId, @Param("dateOfProcedure") LocalDate dateOfProcedure, @Param("category") String category, @Param("toothNumbers") String toothNumber,
      @Param("procedureDone") String procedureDone, @Param("columnValue") String columnValue, Pageable paging);
}
