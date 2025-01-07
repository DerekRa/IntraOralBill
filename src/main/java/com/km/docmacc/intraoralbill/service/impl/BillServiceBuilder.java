package com.km.docmacc.intraoralbill.service.impl;

import com.km.docmacc.intraoralbill.clients.dto.ConditionProcedureResponse;
import com.km.docmacc.intraoralbill.clients.dto.IntraoralExaminationResponse;
import com.km.docmacc.intraoralbill.model.dto.AmountTotal;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdown;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdownResponse;
import com.km.docmacc.intraoralbill.model.entity.AmountCharged;
import com.km.docmacc.intraoralbill.model.entity.AmountPayment;
import com.km.docmacc.intraoralbill.repository.ChargedRepository;
import com.km.docmacc.intraoralbill.repository.PaymentRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BillServiceBuilder extends BillServiceResponseEntity {
  @Autowired
  private ChargedRepository chargedRepository;
  @Autowired
  private PaymentRepository paymentRepository;

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
}
