package com.km.docmacc.intraoralbill.service.impl;

import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CATEGORY_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CHARGED_AMOUNT_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.CREATED_BY_NAME_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.DASH;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.DISCOUNT_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.NOTE_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PAYMENT_AMOUNT_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PROCEDURE_DONE_EMPTY;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.PROFILE_ID_FAILED;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.SAVE;
import static com.km.docmacc.intraoralbill.constants.BillGlobalConstants.TOOTH_NUMBER_EMPTY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class BillServiceResponseEntity {

  protected ResponseEntity<BillBreakdownResponse> validationOnBillBreakdownRequest(Long profileId){
    HttpHeaders headers = new HttpHeaders();
    BillBreakdownResponse billBreakdownResponse = new BillBreakdownResponse();
    if(profileId<= 0L){
      log.warn("The profile ID of getting bill is different.");
      return responseBillBreakdown(billBreakdownResponse, headers, BAD_REQUEST);
    }

    return responseBillBreakdown(billBreakdownResponse, headers, OK);
  }
  protected ResponseEntity<AmountTotal> validationOnAmountTotalRequest(Long profileId){
    HttpHeaders headers = new HttpHeaders();
    AmountTotal amountTotal = new AmountTotal();
    if(profileId<= 0L){
      log.warn("The profile ID of getting bill is different.");
      return responseAmountTotal(amountTotal, headers, BAD_REQUEST);
    }

    return responseAmountTotal(amountTotal, headers, OK);
  }
  protected ResponseEntity<List<AmountChargedResponse>> validationOnAmountChargedRequest(
      PaginationRequest paginationRequest){
    HttpHeaders headers = new HttpHeaders();
    List<AmountChargedResponse> amountChargedResponseList = new ArrayList<AmountChargedResponse>();
    if(paginationRequest.getProfileId() <= 0L){
      log.warn("The Appointment Profile ID to save is different.");
      return responseAmountCharged(BAD_REQUEST, amountChargedResponseList);
    }
    if(paginationRequest.getCategory().isEmpty()){
      log.warn("The Patient Category field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedResponseList);
    }
    if(paginationRequest.getProcedureDone().isEmpty()){
      log.warn("The Patient Procedure Done field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedResponseList);
    }
    if(paginationRequest.getToothNumber() <= 0){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedResponseList);
    }

    return responseAmountCharged(OK, amountChargedResponseList);
  }
  protected ResponseEntity<List<AmountPaymentResponse>> validationOnAmountPaymentRequest(
      PaginationRequest paginationRequest){
    HttpHeaders headers = new HttpHeaders();
    List<AmountPaymentResponse> amountPaymentResponseList = new ArrayList<AmountPaymentResponse>();
    if(paginationRequest.getProfileId() <= 0L){
      log.warn("The Appointment Profile ID to save is different.");
      return responseAmountPayment(BAD_REQUEST, amountPaymentResponseList);
    }
    if(paginationRequest.getCategory().isEmpty()){
      log.warn("The Patient Category field is empty.");
      return responseAmountPayment(BAD_REQUEST, amountPaymentResponseList);
    }
    if(paginationRequest.getProcedureDone().isEmpty()){
      log.warn("The Patient Procedure Done field is empty.");
      return responseAmountPayment(BAD_REQUEST, amountPaymentResponseList);
    }
    if(paginationRequest.getToothNumber() <= 0){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountPayment(BAD_REQUEST, amountPaymentResponseList);
    }

    return responseAmountPayment(OK, amountPaymentResponseList);
  }
  protected ResponseEntity<BillBreakdown> validationOnAmountDataRequest(
      AmountData paginationRequest){
    BillBreakdown billBreakdownResponse = new BillBreakdown();
    if(paginationRequest.getProfileId() <= 0L){
      log.warn("The Appointment Profile ID to save is different.");
      return responseAmountData(BAD_REQUEST, billBreakdownResponse);
    }
    if(paginationRequest.getCategory().isEmpty()){
      log.warn("The Patient Category field is empty.");
      return responseAmountData(BAD_REQUEST, billBreakdownResponse);
    }
    if(paginationRequest.getProcedureDone().isEmpty()){
      log.warn("The Patient Procedure Done field is empty.");
      return responseAmountData(BAD_REQUEST, billBreakdownResponse);
    }
    if(paginationRequest.getToothNumber() <= 0){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountData(BAD_REQUEST, billBreakdownResponse);
    }

    return responseAmountData(OK, billBreakdownResponse);
  }
  protected ResponseEntity<HttpResponse> validationAmountChargedRequest(
      AmountChargedRequest amountChargedRequest){
    if(amountChargedRequest.getProfileId() <= 0L){
      log.warn("The amount charged Profile ID to save is different.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + PROFILE_ID_FAILED);
    }
    if(amountChargedRequest.getChargedAmount().isEmpty()) {
      log.warn("The charged amount being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + CHARGED_AMOUNT_EMPTY);
    }
    if(amountChargedRequest.getDiscount().isEmpty()){
      log.warn("The discount being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + DISCOUNT_EMPTY);
    }
    if(amountChargedRequest.getNote().isEmpty()){
      log.warn("The note being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + NOTE_EMPTY);
    }
    if(amountChargedRequest.getCategory().isEmpty()){
      log.warn("The category being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + CATEGORY_EMPTY);
    }
    if(amountChargedRequest.getProcedureDone().isEmpty()){
      log.warn("The procedure done being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + PROCEDURE_DONE_EMPTY);
    }
    if(amountChargedRequest.getToothNumber() <= 0){
      log.warn("The tooth number done being save is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + TOOTH_NUMBER_EMPTY);
    }
    if(amountChargedRequest.getCreatedByName().isEmpty()){
      log.warn("The created by name is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + CREATED_BY_NAME_EMPTY);
    }
    if(amountChargedRequest.getCreatedById().isEmpty()){
      log.warn("The created by id is empty.");
      return response(BAD_REQUEST, amountChargedRequest.getChargedAmount() + DASH + SAVE + CREATED_BY_NAME_EMPTY);
    }
    return response(OK, "Ok");
  }
  protected ResponseEntity<HttpResponse> validationAmountPaymentRequest(
      AmountPaymentRequest amountPaymentRequest){
    if(amountPaymentRequest.getProfileId() <= 0L){
      log.warn("The amount payment Profile ID to save is different.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + PROFILE_ID_FAILED);
    }
    if(amountPaymentRequest.getPaymentAmount().isEmpty()) {
      log.warn("The payment amount being save is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + PAYMENT_AMOUNT_EMPTY);
    }
    if(amountPaymentRequest.getNote().isEmpty()){
      log.warn("The note being save is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + NOTE_EMPTY);
    }
    if(amountPaymentRequest.getCategory().isEmpty()){
      log.warn("The category being save is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + CATEGORY_EMPTY);
    }
    if(amountPaymentRequest.getProcedureDone().isEmpty()){
      log.warn("The procedure done being save is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + PROCEDURE_DONE_EMPTY);
    }
    if(amountPaymentRequest.getToothNumber() <= 0){
      log.warn("The tooth number done being save is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + TOOTH_NUMBER_EMPTY);
    }
    if(amountPaymentRequest.getCreatedByName().isEmpty()){
      log.warn("The created by name is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + CREATED_BY_NAME_EMPTY);
    }
    if(amountPaymentRequest.getCreatedById().isEmpty()){
      log.warn("The created by id is empty.");
      return response(BAD_REQUEST, amountPaymentRequest.getPaymentAmount() + DASH + SAVE + CREATED_BY_NAME_EMPTY);
    }
    return response(OK, "Ok");
  }

  protected ResponseEntity<BillBreakdownResponse> responseBillBreakdown(BillBreakdownResponse billBreakdownResponse, HttpHeaders headers, HttpStatus httpStatus){
    return new ResponseEntity<BillBreakdownResponse>(billBreakdownResponse, headers, httpStatus);
  }
  protected ResponseEntity<BillBreakdown> responseBillBreakdown(BillBreakdown billBreakdownResponse, HttpHeaders headers, HttpStatus httpStatus){
    return new ResponseEntity<BillBreakdown>(billBreakdownResponse, headers, httpStatus);
  }
  protected ResponseEntity<AmountTotal> responseAmountTotal(AmountTotal amountTotal, HttpHeaders headers, HttpStatus httpStatus){
    return new ResponseEntity<AmountTotal>(amountTotal, headers, httpStatus);
  }
  protected ResponseEntity<List<AmountChargedResponse>> responseAmountCharged(HttpStatus httpStatus, List<AmountChargedResponse> responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<List<AmountChargedResponse>>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<List<AmountPaymentResponse>> responseAmountPayment(HttpStatus httpStatus, List<AmountPaymentResponse> responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<List<AmountPaymentResponse>>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<BillBreakdown> responseAmountData(HttpStatus httpStatus, BillBreakdown responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<BillBreakdown>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
        message), httpStatus);
  }
}
