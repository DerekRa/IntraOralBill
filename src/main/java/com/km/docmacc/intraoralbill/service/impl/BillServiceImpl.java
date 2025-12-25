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
    List<BillBreakdownGroup> intraOralBillBreakdownList = new ArrayList<>();
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
  public ResponseEntity<BillBreakdownGroup> getIntraOralBillBreakdown(AmountData amountData) {
    HttpHeaders headers = new HttpHeaders();
    String amountCharged = "0.0";
    String discount = "0";
    Double amountPaid = 0.0d;
    String payment = "0";

    List<AmountCharged> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(amountData.getProfileId(), amountData.getDateOfProcedure(), amountData.getCategory(), amountData.getToothNumbers(), amountData.getProcedureDone());
    if(optionalAmountChargedList.size() > 0){
      optionalAmountChargedList.sort(
          Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
      Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.stream().findFirst();
      if(optionalAmountCharged.isPresent()){
        amountCharged = optionalAmountCharged.get().getChargedAmount();
        discount = optionalAmountCharged.get().getDiscount();
      }
    }

    List<AmountPayment> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(amountData.getProfileId(), amountData.getDateOfProcedure(), amountData.getCategory(), amountData.getToothNumbers(), amountData.getProcedureDone());
    if(optionalAmountPaymentList.size() > 0){
      optionalAmountPaymentList.sort(Comparator.comparing(AmountPayment::getCreatedDateTime).reversed());
      Optional<AmountPayment> optionalAmountPayment= optionalAmountPaymentList.stream().findFirst();
      if(optionalAmountPayment.isPresent()){
        payment = optionalAmountPayment.get().getPaymentAmount();
        for (AmountPayment amountPayment: optionalAmountPaymentList){
          amountPaid += Double.parseDouble(amountPayment.getPaymentAmount());
        }
      }
    }

    Double balance = (Double.parseDouble(amountCharged) - amountPaid) - Double.parseDouble(discount);

    BillBreakdownGroup breakdown = BillBreakdownGroup.builder()
        .balance(String.valueOf(balance))
        .payment(payment)
        .amountPaid(String.valueOf(amountPaid))
        .amountCharged(amountCharged)
        .discount(discount)
        .procedureDone(amountData.getProcedureDone())
        .toothNumbers(amountData.getToothNumbers())
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
   * @param amountChargedRequest
   * @return
   */
  @Override
  public ResponseEntity<AmountChargedResponse> getAmountCharged(AmountChargedRequest amountChargedRequest) {
    List<AmountCharged> amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(amountChargedRequest.getProfileId(), amountChargedRequest.getDateOfProcedure(),
              amountChargedRequest.getCategory(), amountChargedRequest.getToothNumbers(), amountChargedRequest.getProcedureDone());
    if (amountChargedList.isEmpty()) {
      AmountChargedResponse amountChargedResponse = new AmountChargedResponse();
      amountChargedResponse.setCategory(amountChargedRequest.getCategory());
      amountChargedResponse.setProcedureDone(amountChargedRequest.getProcedureDone());
      amountChargedResponse.setToothNumbers(amountChargedRequest.getToothNumbers());
      return responseAmountCharged(OK, amountChargedResponse);
    }
    amountChargedList.sort(Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
    Optional<AmountCharged> optionalAmountCharged = amountChargedList.stream().findFirst();
    AmountChargedResponse amountChargedResponse = new AmountChargedResponse();
    if(optionalAmountCharged.isPresent()){
      amountChargedResponse.setCategory(amountChargedRequest.getCategory());
      amountChargedResponse.setProcedureDone(amountChargedRequest.getProcedureDone());
      amountChargedResponse.setToothNumbers(amountChargedRequest.getToothNumbers());
      amountChargedResponse.setChargedAmount(optionalAmountCharged.get().getChargedAmount());
      amountChargedResponse.setNote(optionalAmountCharged.get().getNote());
      amountChargedResponse.setDiscount(optionalAmountCharged.get().getDiscount());
      return responseAmountCharged(OK, amountChargedResponse);
    }
    return responseAmountCharged(OK, amountChargedResponse);
  }


  /**
   * @param dataPaginationRequest
   * @return
   */
  @Override
  public ResponseEntity<List<AmountChargedHistoryResponse>> getAmountChargedHistory(
      PaginationRequest dataPaginationRequest) {
    Sort sort = dataPaginationRequest.getSortBy().equalsIgnoreCase(SEARCH_ALL_COLUMNS) ? Sort.by(CREATEDDATETIME).ascending() :
        dataPaginationRequest.getOrderBy().equals(ASC) ? Sort.by(dataPaginationRequest.getSortBy()).ascending() :
            Sort.by(dataPaginationRequest.getSortBy()).descending();
    Pageable paging = PageRequest.of(dataPaginationRequest.getPageNo(), dataPaginationRequest.getPageSize(), sort);
    List<AmountCharged> amountChargedList = new ArrayList<AmountCharged>();
    if(StringUtils.equals(dataPaginationRequest.getFindItem(), ASTERISK)){
      amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
          dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), paging).stream().toList();
    } else {
      if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CHARGEDAMOUNT)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndChargedAmountLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(DISCOUNT)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndDiscountLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(NOTE)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndNoteLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATED_BY_NAME)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedByNameLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATEDDATE)){
        amountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedDate(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), LocalDate.parse(dataPaginationRequest.getFindItem()), paging).toList();
      } else {
        amountChargedList = chargedRepository.findPatientAmountCharged(dataPaginationRequest.getProfileId(),
            dataPaginationRequest.getDateOfProcedure(), dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE +
            dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      }
    }
    log.info("Data of amount charged list ::{}",amountChargedList);
    List<AmountChargedHistoryResponse> amountChargedHistoryResponseList = new ArrayList<AmountChargedHistoryResponse>();
    if(!amountChargedList.isEmpty()){
      for (AmountCharged amountCharged : amountChargedList){
        AmountChargedHistoryResponse amountChargedHistoryResponse = new AmountChargedHistoryResponse();
        BeanUtils.copyProperties(amountCharged, amountChargedHistoryResponse);
        amountChargedHistoryResponseList.add(amountChargedHistoryResponse);
      }
      return responseAmountCharged(OK, amountChargedHistoryResponseList);
    }

    return responseAmountCharged(OK, amountChargedHistoryResponseList);
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
      amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
          dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), paging).stream().toList();
    } else {
      if(dataPaginationRequest.getSortBy().equalsIgnoreCase(PAYMENTAMOUNT)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndPaymentAmountLike(dataPaginationRequest.getProfileId(),dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(NOTE)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndNoteLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATED_BY_NAME)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedByNameLike(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), PERCENTAGE + dataPaginationRequest.getFindItem() + PERCENTAGE, paging).toList();
      } else if(dataPaginationRequest.getSortBy().equalsIgnoreCase(CREATEDDATE)){
        amountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDoneAndCreatedDate(dataPaginationRequest.getProfileId(), dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(), LocalDate.parse(dataPaginationRequest.getFindItem()), paging).toList();
      } else {
        amountPaymentList = paymentRepository.findPatientAmountPayment(dataPaginationRequest.getProfileId(),
            dataPaginationRequest.getDateOfProcedure(),
            dataPaginationRequest.getCategory(), dataPaginationRequest.getToothNumbers(), dataPaginationRequest.getProcedureDone(),
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
            .toothNumbers(amountChargedRequest.getToothNumbers())
            .createdDate(LocalDate.now())
            .createdDateTime(LocalDateTime.now())
            .createdById(amountChargedRequest.getCreatedById())
            .createdByName(amountChargedRequest.getCreatedByName())
        .build());
    sendCommunication(amountChargedSaved.getProfileId(), amountChargedSaved.getDateOfProcedure(), amountChargedSaved.getToothNumbers());
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
            .toothNumbers(amountPaymentRequest.getToothNumbers())
            .createdDate(LocalDate.now())
            .createdDateTime(LocalDateTime.now())
            .createdByName(amountPaymentRequest.getCreatedByName())
            .createdById(amountPaymentRequest.getCreatedById())
        .build());
    sendCommunication(amountPaymentSaved.getProfileId(), amountPaymentSaved.getDateOfProcedure(), amountPaymentSaved.getToothNumbers());
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
