package com.rivigo.riconet.core.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClientContactDTO {
  BigInteger id;
  String type;
  BigInteger level;
  String serviceType;
  String name;
  String phoneNumber;
  Boolean isSSoUserCreated;
  String email;
}
