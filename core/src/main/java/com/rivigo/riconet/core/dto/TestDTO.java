package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by ashfakh on 26/9/17.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TestDTO {
    String message;
    Long retry_time_stamp;
    Long retry_count;
}
