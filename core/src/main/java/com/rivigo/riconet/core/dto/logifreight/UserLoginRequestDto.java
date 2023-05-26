package com.rivigo.riconet.core.dto.logifreight;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class UserLoginRequestDto {
  private UserDto user;

  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @Getter
  @Setter
  public static class UserDto {
    @NotNull private String email;
    @NotNull private String password;
  }
}
