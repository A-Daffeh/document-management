package com.adoustar.documentmanagement.service.impl;

import com.adoustar.documentmanagement.exception.ApiException;
import com.adoustar.documentmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.adoustar.documentmanagement.utils.EmailUtil.getEmailMessage;
import static com.adoustar.documentmanagement.utils.EmailUtil.getResetPasswordMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    static final String PASSWORD_RESET_REQUEST = "Password Reset Request";

    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewAccountEmail(String name, String toEmail, String token) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getEmailMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Unable to send email");
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String name, String toEmail, String token) {
        try {
            var message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setText(getResetPasswordMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Unable to send email");
        }
    }
}
