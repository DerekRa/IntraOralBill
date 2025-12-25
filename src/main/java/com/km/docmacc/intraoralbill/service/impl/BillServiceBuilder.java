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
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;

import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.*;
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

  protected BillBreakdownResponse buildBillBreakdownResponse(List<BillBreakdownGroup> intraOralBillBreakdownList, String bill, String balance, String payment){
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
    if (conditionProcedureResponses == null || conditionProcedureResponses.isEmpty()) {
      return;
    }
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

  protected BillBreakdownResponse buildUpdateBillBreakdownList(List<BillBreakdownGroup> intraOralBillBreakdownList, List<BillBreakdown> intraOralBillBreakdownTempList,
      Long profileId, LocalDate dateOfProcedure){

    /*workout sorting here and create new function to separate*/
    List<BillBreakdownGroup> orderedBreakdownList = formAndSortBillBreakdownList(intraOralBillBreakdownTempList);

    double allBill = 0.0d;
    double allPayment = 0.0d;
    double allBalance = 0.0d;
    //formulate first intraOralBillBreakdownTempList to group by category, procedure done, tooth number

    for (BillBreakdownGroup billBreakdown: orderedBreakdownList){
      String amountCharged = "0.0";
      String discount = "0";
      Double amountPaid = 0.0d;
      String payment = "0";
      Double balance = 0.0d;
      List<AmountCharged> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumbers(), billBreakdown.getProcedureDone());
      if(optionalAmountChargedList.size() > 0){
        optionalAmountChargedList.sort(Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
        Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.stream().findFirst();
        if(optionalAmountCharged.isPresent()){
          amountCharged = optionalAmountCharged.get().getChargedAmount();
          discount = optionalAmountCharged.get().getDiscount();
          billBreakdown.setAmountCharged(amountCharged);
          billBreakdown.setDiscount(discount);
        }
      }
      List<AmountPayment> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumbers(), billBreakdown.getProcedureDone());
      if(optionalAmountPaymentList.size() > 0){
        optionalAmountPaymentList.sort(Comparator.comparing(AmountPayment::getCreatedDateTime).reversed());
        Optional<AmountPayment> optionalAmountPayment= optionalAmountPaymentList.stream().findFirst();
        if(optionalAmountPayment.isPresent()){
          payment = optionalAmountPayment.get().getPaymentAmount();
          billBreakdown.setPayment(payment);
          for (AmountPayment amountPayment: optionalAmountPaymentList){
            amountPaid += Double.parseDouble(amountPayment.getPaymentAmount());
          }
          billBreakdown.setAmountPaid(String.valueOf(amountPaid));
        }
      }
      balance = (Double.parseDouble(amountCharged) - amountPaid) - Double.parseDouble(discount);
      billBreakdown.setBalance(String.valueOf(balance));
      /*intraOralBillBreakdownList.add(BillBreakdownGroup.builder()
              .category(billBreakdown.getCategory())
              .toothNumbers(String.valueOf(billBreakdown.getToothNumbers()))
              .procedureDone(billBreakdown.getProcedureDone())
              .amountCharged(amountCharged)
              .discount(discount)
              .amountPaid(String.valueOf(amountPaid))
              .payment(payment)
              .balance(String.valueOf(balance))
          .build());*/
      allBill += Double.parseDouble(amountCharged) - Double.parseDouble(discount);
      allPayment += amountPaid;
      allBalance += balance;
    }

    /*workout sorting here and create new function to separate*/
//    List<BillBreakdownGroup> orderedBreakdownList = formAndSortBillBreakdownList(intraOralBillBreakdownList);
    return buildBillBreakdownResponse(orderedBreakdownList, String.valueOf(allBill), String.valueOf(allBalance), String.valueOf(allPayment));
  }

  private List<BillBreakdownGroup> formAndSortBillBreakdownList(List<BillBreakdown> intraOralBillBreakdownList) {

    intraOralBillBreakdownList.sort(Comparator.comparing(BillBreakdown::getCategory).thenComparing(BillBreakdown::getProcedureDone).thenComparing(BillBreakdown::getToothNumber).reversed());
    Set<Map<String, String>> categoryProcedureDoneSet = getMapsCategoryProcedureDone(intraOralBillBreakdownList);

    Map<Map<String, String>, Map<String, Set<String>>> groupedTeethNumbers = new HashMap<>();
    Map<Map<String, String>, Double> groupedAmountCharged = new HashMap<>();
    Map<Map<String, String>, Double> groupedDiscount = new HashMap<>();
    Map<Map<String, String>, Double> groupedAmountPaid = new HashMap<>();
    Map<Map<String, String>, Double> groupedPayment = new HashMap<>();
    Map<Map<String, String>, Double> groupedBalance = new HashMap<>();
    for(Map<String, String> categoryProcedureDone: categoryProcedureDoneSet){
      List<String> teethNumbersPerCategoryProcedureDone = new ArrayList<>();
      double amountChargedPerCategoryProcedureDone = 0.0d;
      double discountPerCategoryProcedureDone = 0.0d;
      double amountPaidPerCategoryProcedureDone = 0.0d;
      double paymentPerCategoryProcedureDone = 0.0d;
      double balancePerCategoryProcedureDone = 0.0d;
      for (BillBreakdown billBreakdown: intraOralBillBreakdownList){
        if(billBreakdown.getCategory().equals(categoryProcedureDone.get("category")) &&
            billBreakdown.getProcedureDone().equals(categoryProcedureDone.get("procedureDone"))){
          teethNumbersPerCategoryProcedureDone.add(String.valueOf(billBreakdown.getToothNumber()));
          amountChargedPerCategoryProcedureDone += Double.parseDouble(StringUtils.isEmpty(billBreakdown.getAmountCharged()) ? "0.0" : billBreakdown.getAmountCharged());
          discountPerCategoryProcedureDone += Double.parseDouble(StringUtils.isEmpty(billBreakdown.getDiscount()) ? "0.0" : billBreakdown.getDiscount());
          amountPaidPerCategoryProcedureDone += Double.parseDouble(StringUtils.isEmpty(billBreakdown.getAmountPaid()) ? "0.0" : billBreakdown.getAmountPaid());
          paymentPerCategoryProcedureDone += Double.parseDouble(StringUtils.isEmpty(billBreakdown.getPayment()) ? "0.0" : billBreakdown.getPayment());
          balancePerCategoryProcedureDone += Double.parseDouble(StringUtils.isEmpty(billBreakdown.getBalance()) ? "0.0" : billBreakdown.getBalance());
        }
      }
      groupedTeethNumbers.put(categoryProcedureDone, groupTeethByType(teethNumbersPerCategoryProcedureDone));
      groupedAmountCharged.put(categoryProcedureDone, amountChargedPerCategoryProcedureDone);
      groupedDiscount.put(categoryProcedureDone, discountPerCategoryProcedureDone);
      groupedAmountPaid.put(categoryProcedureDone, amountPaidPerCategoryProcedureDone);
      groupedPayment.put(categoryProcedureDone, paymentPerCategoryProcedureDone);
      groupedBalance.put(categoryProcedureDone, balancePerCategoryProcedureDone);
    }

    log.info("Grouped Teeth by Type : {}", groupedTeethNumbers);

    List<BillBreakdownGroup> uniqueBillBreakdownList = new ArrayList<>();
    for (Map.Entry<Map<String, String>, Map<String, Set<String>>> entry : groupedTeethNumbers.entrySet()) {
      Map<String, String> categoryProcedureDone = entry.getKey();
      Map<String, Set<String>> teethTypeMap = entry.getValue();

      for (Map.Entry<String, Set<String>> teethEntry : teethTypeMap.entrySet()) {
        String teethType = teethEntry.getKey();
        Set<String> teethNumbers = teethEntry.getValue();

        if (!teethNumbers.isEmpty()) {
          List<String> sortedTeethNumbers = new ArrayList<>(teethNumbers);
          sortedTeethNumbers.sort(Comparator.naturalOrder());

          String toothNumbersString = sortedTeethNumbers.toString().replaceAll("[\\[\\]\\s]", "");

          uniqueBillBreakdownList.add(BillBreakdownGroup.builder()
              .category(categoryProcedureDone.get("category"))
              .procedureDone(categoryProcedureDone.get("procedureDone"))
              .toothNumbers(toothNumbersString)
              .build());
        }
      }
    }
    for (BillBreakdownGroup billBreakdownGroup: uniqueBillBreakdownList){
      for (Map.Entry<Map<String, String>, Double> entry : groupedAmountCharged.entrySet()) {
        Map<String, String> keyMap = entry.getKey();
        if(billBreakdownGroup.getCategory().equals(keyMap.get("category")) &&
            billBreakdownGroup.getProcedureDone().equals(keyMap.get("procedureDone"))){
          billBreakdownGroup.setAmountCharged(String.valueOf(groupedAmountCharged.get(keyMap)));
        }
      }
      for (Map.Entry<Map<String, String>, Double> entry : groupedDiscount.entrySet()) {
        Map<String, String> keyMap = entry.getKey();
        if(billBreakdownGroup.getCategory().equals(keyMap.get("category")) &&
            billBreakdownGroup.getProcedureDone().equals(keyMap.get("procedureDone"))){
          billBreakdownGroup.setDiscount(String.valueOf(groupedDiscount.get(keyMap)));
        }
      }
      for (Map.Entry<Map<String, String>, Double> entry : groupedAmountPaid.entrySet()) {
          Map<String, String> keyMap = entry.getKey();
          if(billBreakdownGroup.getCategory().equals(keyMap.get("category")) &&
              billBreakdownGroup.getProcedureDone().equals(keyMap.get("procedureDone"))){
          billBreakdownGroup.setAmountPaid(String.valueOf(groupedAmountPaid.get(keyMap)));
          }
      }
      for (Map.Entry<Map<String, String>, Double> entry : groupedPayment.entrySet()) {
          Map<String, String> keyMap = entry.getKey();
          if(billBreakdownGroup.getCategory().equals(keyMap.get("category")) &&
              billBreakdownGroup.getProcedureDone().equals(keyMap.get("procedureDone"))){
          billBreakdownGroup.setPayment(String.valueOf(groupedPayment.get(keyMap)));
          }
      }
      for (Map.Entry<Map<String, String>, Double> entry : groupedBalance.entrySet()) {
          Map<String, String> keyMap = entry.getKey();
          if(billBreakdownGroup.getCategory().equals(keyMap.get("category")) &&
              billBreakdownGroup.getProcedureDone().equals(keyMap.get("procedureDone"))){
          billBreakdownGroup.setBalance(String.valueOf(groupedBalance.get(keyMap)));
          }
      }
    }

    log.info("Unique Bill Breakdown List : {}", uniqueBillBreakdownList);

    return uniqueBillBreakdownList;
  }

  @NotNull
  private static Set<Map<String, String>> getMapsCategoryProcedureDone(List<BillBreakdown> intraOralBillBreakdownList) {
    Set<Map<String, String>> categoryProcedureDoneSet = new HashSet<>();
    for (BillBreakdown billBreakdown: intraOralBillBreakdownList){
      Map<String, String> categoryProcedureDoneMap = new HashMap<>();
      categoryProcedureDoneMap.put("category", billBreakdown.getCategory());
      categoryProcedureDoneMap.put("procedureDone", billBreakdown.getProcedureDone());
      if(!categoryProcedureDoneSet.contains(categoryProcedureDoneMap)){
        categoryProcedureDoneSet.add(categoryProcedureDoneMap);
      }
    }
    return categoryProcedureDoneSet;
  }

  private Map<String, Set<String>> groupTeethByType(List<String> items) {
    Map<String, Set<String>> groupedTeeth = new HashMap<>();
    groupedTeeth.put("deciduousMaxillary", new HashSet<>());
    groupedTeeth.put("deciduousMandibular", new HashSet<>());
    groupedTeeth.put("permanentMaxillary", new HashSet<>());
    groupedTeeth.put("permanentMandibular", new HashSet<>());

    for (String toothNumber : items) {
      if (DECIDUOUS_TEETH_MAXILLARY.contains(Integer.valueOf(toothNumber))) {
        groupedTeeth.get("deciduousMaxillary").add(toothNumber);
      } else if (DECIDUOUS_TEETH_MANDIBULAR.contains(Integer.valueOf(toothNumber))) {
        groupedTeeth.get("deciduousMandibular").add(toothNumber);
      } else if (PERMANENT_TEETH_MAXILLARY.contains(Integer.valueOf(toothNumber))) {
        groupedTeeth.get("permanentMaxillary").add(toothNumber);
      } else if (PERMANENT_TEETH_MANDIBULAR.contains(Integer.valueOf(toothNumber))) {
        groupedTeeth.get("permanentMandibular").add(toothNumber);
      }
    }
    log.info("Before removing empty groups : {}", groupedTeeth);
    groupedTeeth.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    log.info("Grouped Teeth : {}", groupedTeeth);
    return groupedTeeth;
  }

  protected AmountTotal buildAmountTotals(List<BillBreakdown> intraOralBillBreakdownTempList,
      Long profileId, LocalDate dateOfProcedure){

    List<BillBreakdownGroup> orderedBreakdownList = formAndSortBillBreakdownList(intraOralBillBreakdownTempList);

    double allBill = 0.0d;
    double allPayment = 0.0d;
    double allBalance = 0.0d;
    for (BillBreakdownGroup billBreakdown: orderedBreakdownList){
      String amountCharged = "0.0";
      String discount = "0";
      double amountPaid = 0.0d;
      List<AmountCharged> optionalAmountChargedList = chargedRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumbers(), billBreakdown.getProcedureDone());
      if(optionalAmountChargedList.size() > 0){
        optionalAmountChargedList.sort(Comparator.comparing(AmountCharged::getCreatedDateTime).reversed());
        Optional<AmountCharged> optionalAmountCharged = optionalAmountChargedList.stream().findFirst();
        if(optionalAmountCharged.isPresent()){
          amountCharged = optionalAmountCharged.get().getChargedAmount();
          discount = optionalAmountCharged.get().getDiscount();
        }
      }
      List<AmountPayment> optionalAmountPaymentList = paymentRepository.findByProfileIdAndDateOfProcedureAndCategoryAndToothNumbersAndProcedureDone(profileId, dateOfProcedure, billBreakdown.getCategory(), billBreakdown.getToothNumbers(), billBreakdown.getProcedureDone());
      if(optionalAmountPaymentList.size() > 0){
        optionalAmountPaymentList.sort(Comparator.comparing(AmountPayment::getCreatedDateTime).reversed());
        Optional<AmountPayment> optionalAmountPayment= optionalAmountPaymentList.stream().findFirst();
        if(optionalAmountPayment.isPresent()){
          for (AmountPayment amountPayment: optionalAmountPaymentList){
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
  protected IntraoralTreatmentPlanConsumer buildIntraoralTreatmentPlanConsumer(Long profileId, LocalDate dateOfProcedure, String toothNumbers){
    return IntraoralTreatmentPlanConsumer.builder()
            .profileId(profileId)
            .dateOfProcedure(dateOfProcedure)
            .toothNumbers(toothNumbers)
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
  protected void sendCommunication(Long profileId, LocalDate dateOfProcedure, String toothNumbers){
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

    IntraoralTreatmentPlanConsumer consumerSaved =  intraoralTreatmentPlanConsumerRepository.save(buildIntraoralTreatmentPlanConsumer(profileId, dateOfProcedure, toothNumbers));
    log.info("IntraoralTreatmentPlanConsumer save : "+ consumerSaved);

    IntraoralTreatmentPlanConsumerDto intraoralBillRequestDto = IntraoralTreatmentPlanConsumerDto.builder()
            .consumerId(consumerSaved.getId())
            .profileId(consumerSaved.getProfileId())
            .toothNumbers(consumerSaved.getToothNumbers())
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
