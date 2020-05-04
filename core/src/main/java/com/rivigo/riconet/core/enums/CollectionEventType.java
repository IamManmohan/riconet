package com.rivigo.riconet.core.enums;

/**
 * An enum of types of Collection Events. The action performed on any data set is subject to the
 * type of collection event. Transactions and Tasks are created as per Collection Event Type.
 */
public enum CollectionEventType {
  CREATION,
  EDIT,
  PAYMENT_CASH,
  PAYMENT_SECURITY_DEPOSIT,
  PAYMENT_BANK_TRANSFER,
  PAYMENT_CHEQUE,
  HANDOVER,
  CMS_DEPOSIT_SLIP,
  KNOCK_OFF_CASH,
  KNOCK_OFF_CHEQUE,
  KNOCK_OFF_BANK_TRANSFER,
  CHEQUE_BOUNCE,
  CHEQUE_BOUNCE_BANK_TRANSFER,
  SECURITY_DEPOSIT_ADDITION,
  WRITE_OFF_APPROVED,
  WRITE_OFF_REJECTED,
  PAYMENT_CHANGE_TO_BANK_TRANSFER,
  NORMAL_TO_PAY_TO_NORMAL
}
