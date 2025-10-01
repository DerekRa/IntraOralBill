package com.km.docmacc.intraoralbill.service.impl;

import com.km.docmacc.intraoralbill.clients.IntraOralExaminationClient;
import com.km.docmacc.intraoralbill.clients.dto.ConditionProcedureResponse;
import com.km.docmacc.intraoralbill.clients.dto.IntraoralExaminationResponse;
import com.km.docmacc.intraoralbill.model.dto.*;
import com.km.docmacc.intraoralbill.model.entity.AmountCharged;
import com.km.docmacc.intraoralbill.model.entity.AmountPayment;
import com.km.docmacc.intraoralbill.model.entity.IntraoralTreatmentPlanConsumer;
import com.km.docmacc.intraoralbill.repository.ChargedRepository;
import com.km.docmacc.intraoralbill.repository.IntraoralTreatmentPlanConsumerRepository;
import com.km.docmacc.intraoralbill.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;

import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PENDING;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
public class BillServiceBuilder extends BillServiceResponseEntity {
  @Autowired
  private ChargedRepository chargedRepository;
  @Autowired
  private PaymentRepository paymentRepository;
  @Autowired
  private IntraoralTreatmentPlanConsumerRepository intraoralTreatmentPlanConsumerRepository;
  @Autowired
  private StreamBridge streamBridge;
  @Autowired
  private IntraOralExaminationClient intraOralExaminationClient;

  protected BillBreakdownResponse buildBillBreakdownResponse(List<BillBreakdown> intraOralBillBreakdownList, String bill, String balance, String payment){
    return BillBreakdownResponse.builder()
        .billBreakdowns(intraOralBillBreakdownList)
        .totalBill(bill)
        .totalBalance(balance)
        .totalPayment(payment)
        .build();
  }
  protected AmountTotal buildAmountTotalsResponse(String bill, String balance, String payment){
    return AmountTotal.builder()
        .totalBill(bill)
        .totalBalance(balance)
        .totalPayment(payment)
        .build();
  }
  protected void buildBillBreakdownList(List<BillBreakdown> intraOralBillBreakdownList,
      List<IntraoralExaminationResponse> getIntraoralExaminationList, LocalDate dateOfProcedure) {
    for (IntraoralExaminationResponse intraoralExaminationResponse: getIntraoralExaminationList){
      log.info("date of procedure :{}", intraoralExaminationResponse.getDateOfProcedure());
      if(intraoralExaminationResponse.getDateOfProcedure().equals(dateOfProcedure)){
        List<ConditionProcedureResponse> restorations = intraoralExaminationResponse.getConditionProcedureGroupings().getRestorations();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, restorations);

        List<ConditionProcedureResponse> restorationsInlay = intraoralExaminationResponse.getConditionProcedureGroupings().getRestorationsInlay();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, restorationsInlay);

        List<ConditionProcedureResponse> restorationsOnlay = intraoralExaminationResponse.getConditionProcedureGroupings().getRestorationsOnlay();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, restorationsOnlay);

        List<ConditionProcedureResponse> restorationsFluoride = intraoralExaminationResponse.getConditionProcedureGroupings().getRestorationsFluoride();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, restorationsFluoride);

        List<ConditionProcedureResponse> prosthetics = intraoralExaminationResponse.getConditionProcedureGroupings().getProsthetics();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, prosthetics);

        List<ConditionProcedureResponse> denture = intraoralExaminationResponse.getConditionProcedureGroupings().getDenture();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, denture);

        List<ConditionProcedureResponse> periodontal = intraoralExaminationResponse.getConditionProcedureGroupings().getPeriodontal();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, periodontal);

        List<ConditionProcedureResponse> surgery = intraoralExaminationResponse.getConditionProcedureGroupings().getSurgery();
        buildBillBreakdownListPerConditionProcedure(intraOralBillBreakdownList, intraoralExaminationResponse, surgery);
      }
    }
  }

  private void buildBillBreakdownListPerConditionProcedure(
      List<BillBreakdown> intraOralBillBreakdownList,
      IntraoralExaminationResponse intraoralExaminationResponse,
      List<ConditionProcedureResponse> conditionProcedureResponses) {
    for (ConditionProcedureResponse conditionProcedureResponse: conditionProcedureResponses){
      if(conditionProcedureResponse.getChecked()){
        intraOralBillBreakdownList.add(BillBreakdown.builder()
            .category(conditionProcedureResponse.getGroup())
            .procedureDone(conditionProcedureResponse.getLabel())
            .toothNumber(intraoralExaminationResponse.getDentalChartDesignResponse().getTeethNumbering())
            .build());
      }
    }
  }

  protected BillBreakdownResponse buildUpdateBillBreakdownList(List<BillBreakdown> intraOralBillBreakdownList, List<BillBreakdown> intraOralBillBreakdownTempList,
      Long profileId, LocalDate dateOfProcedure){
    double allBill = 0.0d;
    double allPayment = 0.0d;
    double allBalance = 0.0d;
    for (BillBreakdown billBreakdown: intraOralBillBreakdownTempList){
      String amountCharged = "0.0";
      String discount = "0";
      Double amountPaid = 0.0d;
      String payment = "0";
      Optional<List<AmountCharged>> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumber(), billBreakdown.getProcedureDone());
      if(optionalAmountChargedList.isPresent()){
        optionalAmountChargedList.get().sort(Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
        Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.get().stream().findFirst();
        if(optionalAmountCharged.isPresent()){
          amountCharged = optionalAmountCharged.get().getChargedAmount();
          discount = optionalAmountCharged.get().getDiscount();
        }
      }
      Optional<List<AmountPayment>> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumber(), billBreakdown.getProcedureDone());
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
      intraOralBillBreakdownList.add(BillBreakdown.builder()
              .category(billBreakdown.getCategory())
              .toothNumber(billBreakdown.getToothNumber())
              .procedureDone(billBreakdown.getProcedureDone())
              .amountCharged(amountCharged)
              .discount(discount)
              .amountPaid(String.valueOf(amountPaid))
              .payment(payment)
              .balance(String.valueOf(balance))
          .build());
      allBill += Double.parseDouble(amountCharged) - Double.parseDouble(discount);
      allPayment += amountPaid;
      allBalance += balance;
    }
    return buildBillBreakdownResponse(intraOralBillBreakdownList, String.valueOf(allBill), String.valueOf(allBalance), String.valueOf(allPayment));
  }
  protected AmountTotal buildAmountTotals(List<BillBreakdown> intraOralBillBreakdownTempList,
      Long profileId, LocalDate dateOfProcedure){
    double allBill = 0.0d;
    double allPayment = 0.0d;
    double allBalance = 0.0d;
    for (BillBreakdown billBreakdown: intraOralBillBreakdownTempList){
      String amountCharged = "0.0";
      String discount = "0";
      double amountPaid = 0.0d;
      Optional<List<AmountCharged>> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumber(), billBreakdown.getProcedureDone());
      if(optionalAmountChargedList.isPresent()){
        optionalAmountChargedList.get().sort(Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
        Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.get().stream().findFirst();
        if(optionalAmountCharged.isPresent()){
          amountCharged = optionalAmountCharged.get().getChargedAmount();
          discount = optionalAmountCharged.get().getDiscount();
        }
      }
      Optional<List<AmountPayment>> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumberAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumber(), billBreakdown.getProcedureDone());
      if(optionalAmountPaymentList.isPresent()){
        optionalAmountPaymentList.get().sort(Comparator.comparing(AmountPayment::getCreatedDateTime).reversed());
        Optional<AmountPayment> optionalAmountPayment= optionalAmountPaymentList.get().stream().findFirst();
        if(optionalAmountPayment.isPresent()){
          for (AmountPayment amountPayment: optionalAmountPaymentList.get()){
            amountPaid += Double.parseDouble(amountPayment.getPaymentAmount());
          }
        }
      }
      double balance = (Double.parseDouble(amountCharged) - amountPaid) - Double.parseDouble(discount);
      allBill += Double.parseDouble(amountCharged) - Double.parseDouble(discount);
      allPayment += amountPaid;
      allBalance += balance;
    }
    return buildAmountTotalsResponse(String.valueOf(allBill), String.valueOf(allBalance), String.valueOf(allPayment));
  }
  protected IntraoralTreatmentPlanConsumer buildIntraoralTreatmentPlanConsumer(Long profileId, LocalDate dateOfProcedure, Integer toothNumber){
    return IntraoralTreatmentPlanConsumer.builder()
            .profileId(profileId)
            .dateOfProcedure(dateOfProcedure)
            .toothNumber(toothNumber)
            .consumerStatus(PENDING)
            .communicationSuccessSent(false)
            .createdDateTime(ZonedDateTime.now())
            .createdDate(LocalDate.now())
            .createdById("Intraoral_Bill_Service_ID")
            .createdByName("Intraoral_Bill_Service")
            .build();
  }
  protected IntraoralTreatmentPlanConsumer buildIntraoralTreatmentPlanConsumer(IntraoralTreatmentPlanConsumer updateIntraoralTreatmentPlanConsumer, CommunicationBillSwitchStatus communicationSwitchStatus){
    updateIntraoralTreatmentPlanConsumer.setConsumerStatus(communicationSwitchStatus.getCommunicationStatus());
    updateIntraoralTreatmentPlanConsumer.setUpdatedById("Intraoral_Treatment_Plan_Consumer_Service_ID");
    updateIntraoralTreatmentPlanConsumer.setUpdatedByName("Intraoral_Treatment_Plan_Consumer_Service");
    updateIntraoralTreatmentPlanConsumer.setUpdatedDate(LocalDate.now());
    updateIntraoralTreatmentPlanConsumer.setUpdatedDateTime(ZonedDateTime.now());
    return updateIntraoralTreatmentPlanConsumer;
  }
  protected void sendCommunication(Long profileId, LocalDate dateOfProcedure, Integer toothNumber){
    String totalBill = "0";
    String totalBalance = "0";
    String totalPayment = "0";
    List<BillBreakdown> intraOralBillBreakdownTempList = new ArrayList<>();
    AmountTotal amountTotal = new AmountTotal();
    List<IntraoralExaminationResponse> getIntraoralExaminationList = intraOralExaminationClient.getIntraoralExaminationList(profileId);
    log.info("Data from getIntraoralExaminationList ::{}",getIntraoralExaminationList);
    if(!getIntraoralExaminationList.isEmpty()){
      /*Build category, procedure done, tooth number*/
      buildBillBreakdownList(intraOralBillBreakdownTempList,getIntraoralExaminationList,dateOfProcedure);

      /*Update amount charged, discount, amount paid, payment, balance**/
      amountTotal = buildAmountTotals(intraOralBillBreakdownTempList, profileId, dateOfProcedure);
    } else {
      amountTotal = buildAmountTotalsResponse(totalBill,totalBalance,totalPayment);
    }

    IntraoralTreatmentPlanConsumer consumerSaved =  intraoralTreatmentPlanConsumerRepository.save(buildIntraoralTreatmentPlanConsumer(profileId, dateOfProcedure, toothNumber));
    log.info("IntraoralTreatmentPlanConsumer save : "+ consumerSaved);

    IntraoralTreatmentPlanConsumerDto intraoralBillRequestDto = IntraoralTreatmentPlanConsumerDto.builder()
            .consumerId(consumerSaved.getId())
            .profileId(consumerSaved.getProfileId())
            .toothNumber(consumerSaved.getToothNumber())
            .dateOfProcedure(consumerSaved.getDateOfProcedure())
            .amountTotal(amountTotal)
            .build();
    var communication = streamBridge.send(
            "sendIntraoralBillService-out-0",
            intraoralBillRequestDto
    );
    log.info("Communication sent successfully: {}", communication);
    consumerSaved.setCommunicationSuccessSent(communication);
    intraoralTreatmentPlanConsumerRepository.save(consumerSaved);
  }
}
