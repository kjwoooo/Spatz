package com.elice.spatz.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelErrorResponse {
    private String status;
    private String code;
    private String message;
}
