package com.km.docmacc.intraoralbill.constants;

public class BillGlobalConstants {
  private BillGlobalConstants(){}
  /*Controller*/
  public static final String BREAKDOWN = "breakdown";
  public static final String TOTALS = "totals";
  public static final String TOTAL_BREAKDOWN = "totalBreakdown";
  public static final String AMOUNT_CHARGED = "amountCharged";
  public static final String AMOUNT_PAYMENT = "amountPayment";

  /*Pagination and Sign*/
  public static final String FORWARD_SLASH = "/";
  public static final String ASTERISK = "**";
  public static final String PERCENTAGE = "%";
  public static final String SEARCH_ALL_COLUMNS = "searchAllColumns";
  public static final String ASC = "ASC";
  /*Columns*/
  public static final String CREATEDDATE = "createdDate";
  public static final String CREATEDDATETIME = "createdDateTime";
  public static final String CHARGEDAMOUNT = "chargedAmount";
  public static final String PAYMENTAMOUNT = "paymentAmount";
  public static final String DISCOUNT = "discount";
  public static final String NOTE = "note";
  public static final String CREATED_BY_NAME = "createdByName";
  /*Message Response*/
  public static final String DASH = " - ";
  public static final String SAVE = "Save";
  public static final String SAVE_SUCCESS = " is saved successfully";
  public static final String PROFILE_ID_FAILED = " Failed because your profile ID was different.";
  public static final String CHARGED_AMOUNT_EMPTY = " Failed because charged amount is empty.";
  public static final String PAYMENT_AMOUNT_EMPTY = " Failed because payment amount is empty.";
  public static final String DISCOUNT_EMPTY = " Failed because discount is empty.";
  public static final String NOTE_EMPTY = " Failed because note is empty.";
  public static final String CATEGORY_EMPTY = " Failed because category is empty.";
  public static final String PROCEDURE_DONE_EMPTY = " Failed because procedure done is empty.";
  public static final String TOOTH_NUMBER_EMPTY = " Failed because tooth number is empty.";
  public static final String CREATED_BY_NAME_EMPTY = " Failed because it needs to logout and re-login due to session expired.";
  /*Consumer*/
  public static final String PENDING = "pending";
}
