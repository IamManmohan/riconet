package com.rivigo.riconet.core.enums;

/**
 * An enum of types of Collection Events. The action performed on any data set is subject to the
 * type of collection event. Transactions and Tasks are created as per Collection Event Type.
 */
public enum CollectionEventType {
  CREATION_BY_CAPTAIN,
  CREATION_AT_OU,
  EDIT_BY_CAPTAIN,
  EDIT_AT_OU,
  PAYMENT_CASH_VIA_CAPTAIN,
  PAYMENT_CASH_VIA_DEFAULT_CAPTAIN,
  PAYMENT_SECURITY_DEPOSIT,
  PAYMENT_BANK_TRANSFER,
  PAYMENT_CHEQUE_VIA_CAPTAIN,
  PAYMENT_CHEQUE_VIA_DEFAULT_CAPTAIN,
  PAYMENT_ONLINE,
  HANDOVER_BY_CAPTAIN,
  HANDOVER_BY_DEFAULT_CAPTAIN,
  CMS_DEPOSIT_SLIP,
  CMS_DEPOSIT_SLIP_CANCELLATION,
  KNOCK_OFF_CASH,
  KNOCK_OFF_CHEQUE,
  KNOCK_OFF_BANK_TRANSFER,
  KNOCK_OFF_REVERT_CASH,
  KNOCK_OFF_REVERT_CHEQUE,
  KNOCK_OFF_REVERT_BANK_TRANSFER,
  CHEQUE_BOUNCE,
  CHEQUE_BOUNCE_BANK_TRANSFER,
  SECURITY_DEPOSIT_ADDITION,
  WRITE_OFF_APPROVED,
  WRITE_OFF_REJECTED_VIA_CAPTAIN,
  WRITE_OFF_REJECTED_VIA_DEFAULT_CAPTAIN,
  NORMAL_TO_NORMAL_TO_PAY,
  NORMAL_TO_PAY_TO_NORMAL_VIA_CAPTAIN,
  NORMAL_TO_PAY_TO_NORMAL_VIA_DEFAULT_CAPTAIN,
  PAYMENT_CHANGE_TO_BANK_TRANSFER,
  PAID_TO_TO_PAY_VIA_CAPTAIN,
  PAID_TO_TO_PAY_VIA_DEFAULT_CAPTAIN,
  RETURN_MONEY_COMPLETED
}
