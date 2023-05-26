package com.rivigo.riconet.core.dto.logifreight;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class VyomBaseResponseDto implements Serializable {

  private static final long serialVersionUID = 1012981356588067789L;

  private int code;

  private String message;

  private List<ValidationError> validationErrors;

  class ValidationError {

    private int code;
    private String message;
  }
}
