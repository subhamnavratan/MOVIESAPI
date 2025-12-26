package com.SUBHAM.MOVIEAPI.services;


import com.SUBHAM.MOVIEAPI.dto.MailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailBody.getTo());
        message.setFrom("subhamkumarnavratan@gmail.com");
        message.setSubject(mailBody.getSubject());
        message.setText(mailBody.getMessage());

        javaMailSender.send(message);
    }
}
