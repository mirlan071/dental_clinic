package com.dentalclinic.controller.dto.medicalrecord;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentAttachmentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String description;
    private LocalDateTime createdAt;
}
