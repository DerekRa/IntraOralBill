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

  Optional<List<AmountPayment>> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndPaymentAmountLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String paymentAmount, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndNoteLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String note, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedByNameLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String createdByName, Pageable paging);
  Page<AmountPayment> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedDate(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, LocalDate createdDate, Pageable paging);
  @Query("FROM AmountPayment i WHERE i.profileId = :profileId AND i.dateOfProcedure = :dateOfProcedure AND i.category = :category AND i.toothNumber = :toothNumber AND i.procedureDone = :procedureDone AND " +
      "(i.paymentAmount like :columnValue OR i.note like :columnValue OR i.createdByName like :columnValue)")
  Page<AmountPayment> findPatientAmountPayment(@Param("profileId") Long profileId, @Param("dateOfProcedure") LocalDate dateOfProcedure, @Param("category") String category, @Param("toothNumber") Integer toothNumber,
      @Param("procedureDone") String procedureDone, @Param("columnValue") String columnValue, Pageable paging);
}
