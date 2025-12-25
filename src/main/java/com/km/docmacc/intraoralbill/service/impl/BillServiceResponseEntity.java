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

import com.km.docmacc.intraoralbill.model.dto.*;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class BillServiceResponseEntity {

  protected ResponseEntity<BillBreakdownResponse>  validationOnBillBreakdownRequest(Long profileId){
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
  protected ResponseEntity<AmountChargedResponse> validationOnAmountChargedRequest(
          AmountChargedRequest getAmountCharged){
    AmountChargedResponse amountChargedHistoryResponse = new AmountChargedResponse();
    if(getAmountCharged.getProfileId() <= 0L){
      log.warn("The Appointment Profile ID to save is different.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponse);
    }
    if(getAmountCharged.getCategory().isEmpty()){
      log.warn("The Patient Category field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponse);
    }
    if(getAmountCharged.getProcedureDone().isEmpty()){
      log.warn("The Patient Procedure Done field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponse);
    }
    if(getAmountCharged.getToothNumbers().isEmpty()){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponse);
    }

    return responseAmountCharged(OK, amountChargedHistoryResponse);
  }
  protected ResponseEntity<List<AmountChargedHistoryResponse>> validationOnAmountChargedRequest(
      PaginationRequest paginationRequest){
    HttpHeaders headers = new HttpHeaders();
    List<AmountChargedHistoryResponse> amountChargedHistoryResponseList = new ArrayList<AmountChargedHistoryResponse>();
    if(paginationRequest.getProfileId() <= 0L){
      log.warn("The Appointment Profile ID to save is different.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponseList);
    }
    if(paginationRequest.getCategory().isEmpty()){
      log.warn("The Patient Category field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponseList);
    }
    if(paginationRequest.getProcedureDone().isEmpty()){
      log.warn("The Patient Procedure Done field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponseList);
    }
    if(paginationRequest.getToothNumbers().isEmpty()){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountCharged(BAD_REQUEST, amountChargedHistoryResponseList);
    }

    return responseAmountCharged(OK, amountChargedHistoryResponseList);
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
    if(paginationRequest.getToothNumbers().isEmpty()){
      log.warn("The Patient Tooth Number field is empty.");
      return responseAmountPayment(BAD_REQUEST, amountPaymentResponseList);
    }

    return responseAmountPayment(OK, amountPaymentResponseList);
  }
  protected ResponseEntity<BillBreakdownGroup> validationOnAmountDataRequest(
      AmountData paginationRequest){
    BillBreakdownGroup billBreakdownResponse = new BillBreakdownGroup();
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
    if(paginationRequest.getToothNumbers().isEmpty()){
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
    if(amountChargedRequest.getToothNumbers().isEmpty()){
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
    if(amountPaymentRequest.getToothNumbers().isEmpty()){
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
  protected ResponseEntity<BillBreakdownGroup> responseBillBreakdown(BillBreakdownGroup billBreakdownResponse, HttpHeaders headers, HttpStatus httpStatus){
    return new ResponseEntity<BillBreakdownGroup>(billBreakdownResponse, headers, httpStatus);
  }
  protected ResponseEntity<AmountTotal> responseAmountTotal(AmountTotal amountTotal, HttpHeaders headers, HttpStatus httpStatus){
    return new ResponseEntity<AmountTotal>(amountTotal, headers, httpStatus);
  }
  protected ResponseEntity<AmountChargedResponse> responseAmountCharged(HttpStatus httpStatus, AmountChargedResponse responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<AmountChargedResponse>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<List<AmountChargedHistoryResponse>> responseAmountCharged(HttpStatus httpStatus, List<AmountChargedHistoryResponse> responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<List<AmountChargedHistoryResponse>>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<List<AmountPaymentResponse>> responseAmountPayment(HttpStatus httpStatus, List<AmountPaymentResponse> responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<List<AmountPaymentResponse>>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<BillBreakdownGroup> responseAmountData(HttpStatus httpStatus, BillBreakdownGroup responseDtos) {
    HttpHeaders headers = new HttpHeaders();
    /*headers.setContentType(MediaType.TEXT_HTML);*/
    return new ResponseEntity<BillBreakdownGroup>(responseDtos, headers, httpStatus);
  }
  protected ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
        message), httpStatus);
  }
}
