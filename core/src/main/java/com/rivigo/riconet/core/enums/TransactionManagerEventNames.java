package com.rivigo.riconet.core.enums;

/** This enum contains the event names for transaction manager. */
public enum TransactionManagerEventNames {
  /** CN creation collections event. */
  CN_CREATION_COLLECTIONS,
  /** CN amount change collections event. */
  CN_AMOUNT_CHANGE_COLLECTIONS,
  /** CN payment collections event. */
  CN_PAYMENT_COLLECTIONS,
  /** Handover completed event for consignment. */
  CN_HANDOVER_COMPLETED_COLLECTIONS,
  /** CN type change from normal to normal to pay event. */
  NORMAL_TO_NORMAL_TO_PAY_CHANGE_COLLECTIONS,
  /** CN type change from normal to pay to normal event. */
  NORMAL_TO_PAY_TO_NORMAL_CHANGE_COLLECTIONS,
  /** PAID to TO_PAY type change of consignment. */
  PAID_TO_TO_PAY_CHANGE_COLLECTIONS,
  /** Payment mode change to bank transfer collections event. */
  PAYMENT_CHANGE_TO_BANK_TRANSFER_COLLECTIONS,
  /** Cheque bounce for bank transfer collections event. */
  CHEQUE_BOUNCE_BANK_TRANSFER_COLLECTIONS,
  /** creation of deposit slip collections event. */
  DEPOSIT_SLIP_CREATION_COLLECTIONS,
  /** cancellation of deposit slip collections event. */
  DEPOSIT_SLIP_CANCELLATION_COLLECTIONS,
  /** money returned by captain collections event. */
  RETURN_MONEY_COMPLETED_COLLECTIONS,
  /** invalidation of consignment collections event. */
  CN_INVALIDATION_COLLECTIONS,
  /** Captain assigned for to pay consignment collections event. */
  TO_PAY_CAPTAIN_ASSIGN_COLLECTIONS,
  /** bank transfer approval collections event. */
  BANK_TRANSFER_APPROVAL_COLLECTIONS,
  /** bank transfer rejection collections event. */
  BANK_TRANSFER_REJECTION_COLLECTIONS,
  /** bank transfer UTR knockoff collections event. */
  BANK_TRANSFER_UTR_KNOCKOFF_COLLECTIONS,
  /** bank transfer UTR revert knockoff collections event. */
  BANK_TRANSFER_UTR_REVERT_KNOCKOFF_COLLECTIONS,
  /** bank transfer UTR rejection collections event. */
  BANK_TRANSFER_UTR_REJECTION_COLLECTIONS,
  /** CN amount change and payment type change collections event. */
  CN_AMOUNT_AND_PAYMENT_TYPE_CHANGE_COLLECTIONS,
}
