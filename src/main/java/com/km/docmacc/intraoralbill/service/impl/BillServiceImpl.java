package com.km.docmacc.intraoralbill.service.impl;

import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.ASC;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.ASTERISK;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CHARGEDAMOUNT;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CREATEDDATE;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CREATEDDATETIME;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CREATED_BY_NAME;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.DISCOUNT;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.NOTE;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PAYMENTAMOUNT;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PERCENTAGE;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.SAVE_SUCCESS;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.SEARCH_ALL_COLUMNS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.km.docmacc.intraoralbill.clients.IntraOralExaminationClient;
import com.km.docmacc.intraoralbill.clients.dto.IntraoralExaminationResponse;
import com.km.docmacc.intraoralbill.model.dto.*;
import com.km.docmacc.intraoralbill.model.entity.AmountCharged;
import com.km.docmacc.intraoralbill.model.entity.AmountPayment;
import com.km.docmacc.intraoralbill.model.entity.IntraoralTreatmentPlanConsumer;
import com.km.docmacc.intraoralbill.repository.ChargedRepository;
import com.km.docmacc.intraoralbill.repository.IntraoralTreatmentPlanConsumerRepository;
import com.km.docmacc.intraoralbill.repository.PaymentRepository;
import com.km.docmacc.intraoralbill.service.BillService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillServiceImpl extends BillServiceBuilder implements BillService {
  @Autowired
  private PaymentRepository paymentRepository;
  @Autowired
  private ChargedRepository chargedRepository;
  @Autowired
  private IntraOralExaminationClient intraOralExaminationClient;
  @Autowired
  private IntraoralTreatmentPlanConsumerRepository intraoralTreatmentPlanConsumerRepository;

  /**
   * @param profileId
   * @param dateOfProcedure
   * @return
   */
  @Override
  public ResponseEntity<BillBreakdownResponse> getIntraOralBill(Long profileId,
      LocalDate dateOfProcedure) {
    HttpHeaders headers = new HttpHeaders();
    String totalBill = "0";
    String totalBalance = "0";
    String totalPayment = "0";
    List<BillBreakdown> intraOralBillBreakdownTempList = new ArrayList<>();
    List<BillBreakdown> intraOralBillBreakdownList = new ArrayList<>();
    List<IntraoralExaminationResponse> getIntraoralExaminationList = intraOralExaminationClient.getIntraoralExaminationList(profileId);
    log.info("Data from getIntraoralExaminationList ::{}",getIntraoralExaminationList);
    if(!getIntraoralExaminationList.isEmpty()){
      /*Build category, procedure done, tooth number*/
      buildBillBreakdownList(intraOralBillBreakdownTempList,getIntraoralExaminationList,dateOfProcedure);

      /*Update amount charged, discount, amount paid, payment, balance**/
      return responseBillBreakdown(buildUpdateBillBreakdownList(intraOralBillBreakdownList, intraOralBillBreakdownTempList, profileId, dateOfProcedure), headers, OK);
    }
    log.error("No data found on getIntraoralExaminationList.");
    return responseBillBreakdown(buildBillBreakdownResponse(intraOralBillBreakdownList,totalBill,totalBalance,totalPayment), headers, BAD_REQUEST);
  }

  /**
   * @param amountData
   * @return
   */
  @Override
  public ResponseEntity<BillBreakdown> getIntraOralBillBreakdown(AmountData amountData) {
    HttpHeaders headers = new HttpHeaders();
    String amountCharged = "0.0";
    String discount = "0";
    Double amountPaid = 0.0d;
    String payment = "0";

    Optional<List<AmountCharged>> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(amountData.getProfileId(), amountData.getDateOfProcedure(), amountData.getCategory(), amountData.getToothNumber(), amountData.getProcedureDone());
    if(optionalAmountChargedList.isPresent()){
      optionalAmountChargedList.get().sort(
          Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
      Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.get().stream().findFirst();
      if(optionalAmountCharged.isPresent()){
        amountCharged = optionalAmountCharged.get().getChargedAmount();
        discount = optionalAmountCharged.get().getDiscount();
      }
    }

    Optional<List<AmountPayment>> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(amountData.getProfileId(), amountData.getDateOfProcedure(), amountData.getCategory(), amountData.getToothNumber(), amountData.getProcedureDone());
    if(optionalAmountPaymentList.isPresent()){
      optionalAmountPaymentList.get().sort(Comparator.comparing(AmountPayment::getCreatedDateTime).reversed());
      Optional<AmountPayment> optionalAmountPayment= optionalAmountPaymentList.get().stream().findFirst();
      if(optionalAmountPayment.isPresent()){
        payment = optionalAmountPayment.get().getPaymentAmount();
        for (AmountPayment amountPayment: optionalAmountPaymentList.get()){
          amountPaid += Double.parseDouble(amountPayment.getPaymentAmount());
        }
      }
    }

    Double balance = (Double.parseDouble(amountCharged) - amountPaid) - Double.parseDouble(discount);

    BillBreakdown breakdown = BillBreakdown.builder()
        .balance(String.valueOf(balance))
        .payment(payment)
        .amountPaid(String.valueOf(amountPaid))
        .amountCharged(amountCharged)
        .discount(discount)
        .procedureDone(amountData.getProcedureDone())
        .toothNumber(amountData.getToothNumber())
        .category(amountData.getCategory())
        .build();

    return responseBillBreakdown(breakdown, headers, OK);
  }

  /**
   * @param profileId
   * @param dateOfProcedure
   * @return
   */
  @Override
  public ResponseEntity<AmountTotal> getIntraOralAmountTotals(Long profileId, LocalDate dateOfProcedure) {
    HttpHeaders headers = new HttpHeaders();
    String totalBill = "0";
    String totalBalance = "0";
    String totalPayment = "0";
    List<BillBreakdown> intraOralBillBreakdownTempList = new ArrayList<>();
    List<IntraoralExaminationResponse> getIntraoralExaminationList = intraOralExaminationClient.getIntraoralExaminationList(profileId);
    log.info("Data from getIntraoralExaminationList ::{}",getIntraoralExaminationList);
    if(!getIntraoralExaminationList.isEmpty()){
      /*Build category, procedure done, tooth number*/
      buildBillBreakdownList(intraOralBillBreakdownTempList,getIntraoralExaminationList,dateOfProcedure);

      /*Update amount charged, discount, amount paid, payment, balance**/
      return responseAmountTotal(buildAmountTotals(intraOralBillBreakdownTempList, profileId, dateOfProcedure), headers, OK);
    }
    log.error("No data found on getIntraoralExaminationList.");
    return responseAmountTotal(buildAmountTotalsResponse(totalBill,totalBalance,totalPayment), headers, BAD_REQUEST);
  }


  /**
   * @param dataPaginationRequest
   * @return
   */
  @Override
  public ResponseEntity<List<AmountChargedResponse>> getAmountCharged(
      PaginationRequest dataPaginationRequest) {
    Sort sort = dataPaginationRequest.getSortBy().equalsIgnoreCase(SEARCH_ALL_COLUMNS) ? Sort.by(CREATEDDATETIME).ascending() :
        dataPaginationRequest.getOrderBy().equals(ASC) ? Sort.by(dataPaginationRequest.getSortBy()).ascending() :
            Sort.by(dataPaginationRequest.getSortBy()).descending();
    Pageable paging = PageRequest.of(dataPaginationRequest.getPageNo(), dataPaginationRequest.getPageSize(), sort);
    List<AmountCharged> amountChargedList = new ArrayList<AmountCharged>();
    if(StringUtils.equals(dataPaginationRequest.getFindItem(), ASTERISK)){
      amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
          dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), paging).stream().toList();
    } else {
      if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CHARGEDAMOUNT)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndChargedAmountLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(DISCOUNT)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndDiscountLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(NOTE)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndNoteLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATED_BY_NAME)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedByNameLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATEDDATE)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedDate(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), LocalDate.parse(dataPaginationRequest.getFindItem()), paging).toList();
      } else {
        amountChargedList = chargedRepository.findPatientAmountCharged(dataPaginationRequest.getProfileId(),
            dataPaginationRequest.getDateOfProcedure(), dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      }
    }
    log.info("Data of amount charged list ::{}",amountChargedList);
    List<AmountChargedResponse> amountChargedResponseList = new ArrayList<AmountChargedResponse>();
    if(!amountChargedList.isEmpty()){
      for (AmountCharged amountCharged : amountChargedList){
        AmountChargedResponse amountChargedResponse = new AmountChargedResponse();
        BeanUtils.copyProperties(amountCharged, amountChargedResponse);
        amountChargedResponseList.add(amountChargedResponse);
      }
      return responseAmountCharged(OK, amountChargedResponseList);
    }

    return responseAmountCharged(OK, amountChargedResponseList);
  }

  /**
   * @param dataPaginationRequest
   * @return
   */
  @Override
  public ResponseEntity<List<AmountPaymentResponse>> getAmountPayment(
      PaginationRequest dataPaginationRequest) {
    Sort sort = dataPaginationRequest.getSortBy().equalsIgnoreCase(SEARCH_ALL_COLUMNS) ? Sort.by(CREATEDDATETIME).ascending() :
        dataPaginationRequest.getOrderBy().equals(ASC) ? Sort.by(dataPaginationRequest.getSortBy()).ascending() :
            Sort.by(dataPaginationRequest.getSortBy()).descending();
    Pageable paging = PageRequest.of(dataPaginationRequest.getPageNo(), dataPaginationRequest.getPageSize(), sort);
    List<AmountPayment> amountPaymentList;
    if(StringUtils.equals(dataPaginationRequest.getFindItem(), ASTERISK)){
      amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
          dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), paging).stream().toList();
    } else {
      if(dataPaginationRequest.getSortBy().equalsIgnoreCase(PAYMENTAMOUNT)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndPaymentAmountLike(dataPaginationRequest.getProfileId(),dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(NOTE)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndNoteLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATED_BY_NAME)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedByNameLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATEDDATE)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDoneAndCreatedDate(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(), LocalDate.parse(dataPaginationRequest.getFindItem()), paging).toList();
      } else {
        amountPaymentList = paymentRepository.findPatientAmountPayment(dataPaginationRequest.getProfileId(),
            dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumber(), dataPaginationRequest.getProcedureDone(),
            PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      }
    }
    log.info("Data of amount payment list ::{}",amountPaymentList);
    List<AmountPaymentResponse> amountPaymentResponseList = new ArrayList<AmountPaymentResponse>();
    if(!amountPaymentList.isEmpty()){
      for (AmountPayment amountPayment : amountPaymentList){
        AmountPaymentResponse amountPaymentResponse = new AmountPaymentResponse();
        BeanUtils.copyProperties(amountPayment, amountPaymentResponse);
        amountPaymentResponseList.add(amountPaymentResponse);
      }
      return responseAmountPayment(OK, amountPaymentResponseList);
    }

    return responseAmountPayment(OK, amountPaymentResponseList);
  }

  /**
   * @param amountChargedRequest
   * @return
   */
  @Override
  public ResponseEntity<HttpResponse> insertAmountCharged(
      AmountChargedRequest amountChargedRequest) {
    AmountCharged amountChargedSaved = chargedRepository.save(AmountCharged.builder()
            .chargedAmount(amountChargedRequest.getChargedAmount())
            .note(amountChargedRequest.getNote())
            .profileId(amountChargedRequest.getProfileId())
            .dateOfProcedure(amountChargedRequest.getDateOfProcedure())
            .discount(amountChargedRequest.getDiscount())
            .category(amountChargedRequest.getCategory())
            .procedureDone(amountChargedRequest.getProcedureDone())
            .toothNumber(amountChargedRequest.getToothNumber())
            .createdDate(LocalDate.now())
            .createdDateTime(LocalDateTime.now())
            .createdById(amountChargedRequest.getCreatedById())
            .createdByName(amountChargedRequest.getCreatedByName())
        .build());
    sendCommunication(amountChargedSaved.getProfileId(), amountChargedSaved.getDateOfProcedure(), amountChargedSaved.getToothNumber());
    return response(CREATED, "Charged Amount " + amountChargedSaved.getChargedAmount() + SAVE_SUCCESS);
  }

  /**
   * @param amountPaymentRequest
   * @return
   */
  @Override
  public ResponseEntity<HttpResponse> insertAmountPayment(
      AmountPaymentRequest amountPaymentRequest) {
    AmountPayment amountPaymentSaved = paymentRepository.save(AmountPayment.builder()
            .profileId(amountPaymentRequest.getProfileId())
            .dateOfProcedure(amountPaymentRequest.getDateOfProcedure())
            .paymentAmount(amountPaymentRequest.getPaymentAmount())
            .note(amountPaymentRequest.getNote())
            .category(amountPaymentRequest.getCategory())
            .procedureDone(amountPaymentRequest.getProcedureDone())
            .toothNumber(amountPaymentRequest.getToothNumber())
            .createdDate(LocalDate.now())
            .createdDateTime(LocalDateTime.now())
            .createdByName(amountPaymentRequest.getCreatedByName())
            .createdById(amountPaymentRequest.getCreatedById())
        .build());
    sendCommunication(amountPaymentSaved.getProfileId(), amountPaymentSaved.getDateOfProcedure(), amountPaymentSaved.getToothNumber());
    return response(CREATED, "Payment Amount " + amountPaymentSaved.getPaymentAmount() + SAVE_SUCCESS);
  }

  /**
   * @param communicationSwitchStatus
   */
  @Override
  public void updateIntraoralTreatmentPlanConsumer(CommunicationBillSwitchStatus communicationSwitchStatus) {
    Optional<IntraoralTreatmentPlanConsumer> intraoralTreatmentPlanConsumer = intraoralTreatmentPlanConsumerRepository.findById(communicationSwitchStatus.getIntraoralBillConsumerId());
    if(intraoralTreatmentPlanConsumer.isPresent()){
      intraoralTreatmentPlanConsumerRepository.save(buildIntraoralTreatmentPlanConsumer(intraoralTreatmentPlanConsumer.get(), communicationSwitchStatus));
    } else {
      log.error("No Intraoral Treatment Plan Consumer found for id : {}", communicationSwitchStatus.getIntraoralBillConsumerId());
    }
  }
}
