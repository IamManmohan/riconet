package com.rivigo.riconet.core.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by ashfakh on 13/09/18. */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientCodDodDTO extends ClientVasDetailDTO {
  String inFavourOf;
  String plotNo;
  String street;
  String landmark;
  String district;
  String state;
  String pincode;
  String mobileNumber;
  String clientAddressId;
}
