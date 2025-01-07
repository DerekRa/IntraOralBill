package com.km.docmacc.intraoralbill.service;

import com.km.docmacc.intraoralbill.model.dto.AmountChargedRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountChargedResponse;
import com.km.docmacc.intraoralbill.model.dto.AmountData;
import com.km.docmacc.intraoralbill.model.dto.PaginationRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountPaymentRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountPaymentResponse;
import com.km.docmacc.intraoralbill.model.dto.AmountTotal;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdown;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdownResponse;
import com.km.docmacc.intraoralbill.model.dto.HttpResponse;
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

}
