package com.km.docmacc.intraoralbill.repository;

import com.km.docmacc.intraoralbill.model.entity.AmountCharged;
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
public interface ChargedRepository extends
    JpaRepository<AmountCharged, Long> {

  Optional<List<AmountCharged>> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, Pageable paging);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndChargedAmountLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String chargedAmount, Pageable paging);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndDiscountLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String discount, Pageable paging);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndNoteLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String note, Pageable paging);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedByNameLike(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, String createdByName, Pageable paging);
  Page<AmountCharged> findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedDate(Long profileId, LocalDate dateOfProcedure, String category, Integer toothNumber, String procedureDone, LocalDate createdDate, Pageable paging);
  @Query("FROM AmountCharged i WHERE i.profileId = :profileId AND i.dateOfProcedure = :dateOfProcedure AND i.category = :category AND i.toothNumber = :toothNumber AND i.procedureDone = :procedureDone AND " +
      "(i.chargedAmount like :columnValue OR i.discount like :columnValue OR i.note like :columnValue OR i.createdByName like :columnValue)")
  Page<AmountCharged> findPatientAmountCharged(@Param("profileId") Long profileId, @Param("dateOfProcedure")  LocalDate dateOfProcedure, @Param("category") String category, @Param("toothNumber") Integer toothNumber,
      @Param("procedureDone") String procedureDone, @Param("columnValue") String columnValue, Pageable paging);
}
