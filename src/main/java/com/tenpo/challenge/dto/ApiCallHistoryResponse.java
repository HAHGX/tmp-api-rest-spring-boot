package com.tenpo.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallHistoryResponse {
    private Long id;
    private String endpoint;
    private String method;
    private String requestParams;
    private String responseBody;
    private Integer statusCode;
    private String errorMessage;
    private ZonedDateTime createdAt;
}