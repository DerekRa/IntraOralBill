package com.km.docmacc.intraoralbill.service;

import com.km.docmacc.intraoralbill.model.dto.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface BillService {

  ResponseEntity<BillBreakdownResponse> getIntraOralBill(Long profileId, LocalDate dateOfProcedure);
  ResponseEntity<BillBreakdown> getIntraOralBillBreakdown(AmountData amountData);
  ResponseEntity<AmountTotal> getIntraOralAmountTotals(Long profileId, LocalDate dateOfProcedure);
  ResponseEntity<List<AmountChargedResponse>> getAmountCharged(
      PaginationRequest dataPaginationRequest);
  ResponseEntity<List<AmountPaymentResponse>> getAmountPayment(
      PaginationRequest dataPaginationRequest);
  ResponseEntity<HttpResponse> insertAmountCharged(AmountChargedRequest amountChargedRequest);
  ResponseEntity<HttpResponse> insertAmountPayment(AmountPaymentRequest amountPaymentRequest);
  void updateIntraoralTreatmentPlanConsumer(CommunicationBillSwitchStatus communicationSwitchStatus);
}
