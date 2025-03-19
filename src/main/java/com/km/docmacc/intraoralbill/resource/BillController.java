package com.km.docmacc.intraoralbill.resource;

import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.AMOUNT_CHARGED;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.AMOUNT_PAYMENT;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.BREAKDOWN;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.FORWARD_SLASH;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.TOTALS;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.TOTAL_BREAKDOWN;
import static org.springframework.http.HttpStatus.OK;

import com.km.docmacc.intraoralbill.model.dto.AmountChargedRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountChargedResponse;
import com.km.docmacc.intraoralbill.model.dto.AmountData;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdown;
import com.km.docmacc.intraoralbill.model.dto.PaginationRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountPaymentRequest;
import com.km.docmacc.intraoralbill.model.dto.AmountPaymentResponse;
import com.km.docmacc.intraoralbill.model.dto.AmountTotal;
import com.km.docmacc.intraoralbill.model.dto.BillBreakdownResponse;
import com.km.docmacc.intraoralbill.model.dto.HttpResponse;
import com.km.docmacc.intraoralbill.service.BillService;
import com.km.docmacc.intraoralbill.service.impl.BillServiceResponseEntity;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/intraOralBill")
public class BillController extends BillServiceResponseEntity {

  private final BillService billService;

  @Autowired
  public BillController(BillService billService) {
    this.billService = billService;
  }
  @GetMapping(FORWARD_SLASH + TOTAL_BREAKDOWN)
  public ResponseEntity<BillBreakdownResponse> getBillBreakdownList(@RequestParam Long profileId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfProcedure){
    log.info("Get Bill Total and Breakdown with Request parameters are profile ID: {} and dateOfProcedure : {}", profileId, dateOfProcedure);
    return validationOnBillBreakdownRequest(profileId).getStatusCode().equals(OK) ?
        billService.getIntraOralBill(profileId, dateOfProcedure) :
        validationOnBillBreakdownRequest(profileId);
  }
  @GetMapping(FORWARD_SLASH + BREAKDOWN)
  public ResponseEntity<BillBreakdown> getBillBreakdown(@RequestParam Long profileId, @RequestParam LocalDate dateOfProcedure,
      @RequestParam String category, @RequestParam String procedureDone, @RequestParam Integer toothNumber){
    log.info("Get Bill Breakdown Data Request profileId:{}, dateOfProcedure:{} category:{}, procedureDone:{}, toothNumber:{}",
        profileId,dateOfProcedure,category,procedureDone,toothNumber);
    AmountData amountData = AmountData.builder()
        .profileId(profileId)
        .dateOfProcedure(dateOfProcedure)
        .category(category)
        .procedureDone(procedureDone)
        .toothNumber(toothNumber)
        .build();
    return validationOnAmountDataRequest(amountData).getStatusCode().equals(OK) ?
        billService.getIntraOralBillBreakdown(amountData) :
        validationOnAmountDataRequest(amountData);
  }
  @GetMapping(FORWARD_SLASH + TOTALS)
  public ResponseEntity<AmountTotal> getAmountTotals(@RequestParam Long profileId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfProcedure){
    log.info("Request parameters are profile ID: {} and dateOfProcedure : {}", profileId, dateOfProcedure);
    return validationOnAmountTotalRequest(profileId).getStatusCode().equals(OK) ?
        billService.getIntraOralAmountTotals(profileId, dateOfProcedure) :
        validationOnAmountTotalRequest(profileId);
  }
  @GetMapping(FORWARD_SLASH + AMOUNT_CHARGED)
  public ResponseEntity<List<AmountChargedResponse>> getBillAmountCharged(@RequestParam Long profileId, @RequestParam LocalDate dateOfProcedure,
      @RequestParam String category, @RequestParam String procedureDone, @RequestParam Integer toothNumber,
      @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam String sortBy,
      @RequestParam String orderBy, @RequestParam String findItem){
    log.info("Get Bill Amount Charged History Data Request profileId:{}, dateOfProcedure:{} category:{}, procedureDone:{}, toothNumber:{}, orderBy:{}, findItem:{}, sortBy:{}, pageNo:{}, pageSize:{}",
        profileId,dateOfProcedure,category,procedureDone,toothNumber,orderBy,findItem,sortBy,pageNo,pageSize);
    PaginationRequest paginationRequest = PaginationRequest.builder()
        .profileId(profileId)
        .dateOfProcedure(dateOfProcedure)
        .category(category)
        .procedureDone(procedureDone)
        .toothNumber(toothNumber)
        .orderBy(orderBy)
        .findItem(findItem)
        .sortBy(sortBy)
        .pageNo(pageNo)
        .pageSize(pageSize)
        .build();
    return validationOnAmountChargedRequest(paginationRequest).getStatusCode().equals(OK) ?
        billService.getAmountCharged(paginationRequest) :
        validationOnAmountChargedRequest(paginationRequest);
  }
  @GetMapping(FORWARD_SLASH + AMOUNT_PAYMENT)
  public ResponseEntity<List<AmountPaymentResponse>> getBillAmountPayment(@RequestParam Long profileId, @RequestParam LocalDate dateOfProcedure,
      @RequestParam String category, @RequestParam String procedureDone, @RequestParam Integer toothNumber,
      @RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam String sortBy,
      @RequestParam String orderBy, @RequestParam String findItem){
    log.info("Get Bill Amount Payment History Data Request profileId:{}, dateOfProcedure:{}, category:{}, procedureDone:{}, toothNumber:{}, orderBy:{}, findItem:{}, sortBy:{}, pageNo:{}, pageSize:{}",
        profileId,dateOfProcedure,category,procedureDone,toothNumber,orderBy,findItem,sortBy,pageNo,pageSize);
    PaginationRequest paginationRequest = PaginationRequest.builder()
        .profileId(profileId)
        .dateOfProcedure(dateOfProcedure)
        .category(category)
        .procedureDone(procedureDone)
        .toothNumber(toothNumber)
        .orderBy(orderBy)
        .findItem(findItem)
        .sortBy(sortBy)
        .pageNo(pageNo)
        .pageSize(pageSize)
        .build();
    return validationOnAmountPaymentRequest(paginationRequest).getStatusCode().equals(OK) ?
        billService.getAmountPayment(paginationRequest) :
        validationOnAmountPaymentRequest(paginationRequest);
  }
  @PostMapping(FORWARD_SLASH + AMOUNT_CHARGED)
  public ResponseEntity<HttpResponse> saveAmountCharged(@RequestBody AmountChargedRequest amountChargedRequest){
    log.info("Save amount charged data ::{}",amountChargedRequest);
    return validationAmountChargedRequest(amountChargedRequest).getStatusCode().equals(OK) ?
        billService.insertAmountCharged(amountChargedRequest) :
        validationAmountChargedRequest(amountChargedRequest);
  }
  @PostMapping(FORWARD_SLASH + AMOUNT_PAYMENT)
  public ResponseEntity<HttpResponse> saveAmountPayment(@RequestBody AmountPaymentRequest amountPaymentRequest){
    log.info("Save amount payment data ::{}",amountPaymentRequest);
    return validationAmountPaymentRequest(amountPaymentRequest).getStatusCode().equals(OK) ?
        billService.insertAmountPayment(amountPaymentRequest) :
        validationAmountPaymentRequest(amountPaymentRequest);
  }
}
