package com.rivigo.riconet.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class FinanceUtils {

  @Autowired private static ObjectMapper objectMapper;

  private FinanceUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String createToken(
      String orgId, String functionType, String tenantType, String zoombookClientSecret) {
    try {
      return buildChecksum(Arrays.asList(orgId, functionType, tenantType, zoombookClientSecret));
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }
    return null;
  }

  public static String buildChecksum(List<String> entities)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    Collections.sort(entities);
    String listString = entities.stream().map(Object::toString).collect(Collectors.joining(""));
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    byte[] sha1Hash;
    md.update(listString.getBytes("iso-8859-1"), 0, listString.length());
    sha1Hash = md.digest();
    return convertToHex(sha1Hash);
  }

  private static String convertToHex(byte[] data) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      int halfByte = (data[i] >>> 4) & 0x0F;
      int twoHalves = 0;
      do {
        if ((0 <= halfByte) && (halfByte <= 9)) {
          buf.append((char) ('0' + halfByte));
        } else {
          buf.append((char) ('a' + (halfByte - 10)));
        }
        halfByte = data[i] & 0x0F;
      } while (twoHalves++ < 1);
    }
    return buf.toString();
  }

  /**
   * function that coverts the dto String fetched from compass to desired DTO.
   *
   * @author Nikhil Rawat on 26/05/20.
   */
  public static <T> T getDtoFromjsonString(String dtoString, Class<?> target)
      throws ClassNotFoundException {
    try {
      return (T) objectMapper.readValue(dtoString, Class.forName(target.getName()));
    } catch (IOException ex) {
      log.error("Error occured while processing message {} ", dtoString, ex);
      return null;
    }
  }
}
