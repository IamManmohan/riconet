package com.rivigo.riconet.core.client;

import static com.rivigo.zoom.util.rest.constants.ResponseJavaTypes.TYPE_FACTORY;

import com.fasterxml.jackson.databind.JavaType;
import com.rivigo.riconet.core.constants.LogiFreightConstants;
import com.rivigo.riconet.core.dto.logifreight.RecordDeliveryResponseDto;
import com.rivigo.riconet.core.dto.logifreight.ReleaseLrHoldResponseDto;
import com.rivigo.riconet.core.dto.logifreight.UploadPodResponseDto;
import com.rivigo.riconet.core.dto.logifreight.UserLoginResponseDto;
import com.rivigo.zoom.util.rest.enums.RetryRestRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public enum LogiFreightRestServiceRequest implements RetryRestRequest {
  GET_USER_LOGIN_DETAILS(
      LogiFreightConstants.USER_LOGIN_URL,
      HttpMethod.POST,
      TYPE_FACTORY.constructType(UserLoginResponseDto.class),
      true),

  RECORD_CONSIGNMENT_DELIVERY(
      LogiFreightConstants.RECORD_DELIVERY_URL,
      HttpMethod.POST,
      TYPE_FACTORY.constructType(RecordDeliveryResponseDto.class),
      true),

  UPLOAD_POD(
      LogiFreightConstants.UPLOAD_POD_URL,
      HttpMethod.POST,
      TYPE_FACTORY.constructType(UploadPodResponseDto.class),
      true),

  RELEASE_HOLD(
      LogiFreightConstants.RELEASE_HOLD_URL,
      HttpMethod.POST,
      TYPE_FACTORY.constructType(ReleaseLrHoldResponseDto.class),
      false);

  @NonNull private final String endpoint;
  @NonNull private final HttpMethod httpMethod;
  @NonNull private final JavaType returnType;
  private final boolean retryEndpoint;
}
