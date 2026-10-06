package com.scentiva.modules.notification.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayload {
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String htmlContent;
    private String textContent;
    private String fromName;
    private String fromEmail;
    private String replyTo;
    private Map<String, String> headers;
}
