package com.SUBHAM.MOVIEAPI.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MailBody {
    private String to;
    private String subject;
    private String message;
}