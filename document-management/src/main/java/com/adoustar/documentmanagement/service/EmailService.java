package com.adoustar.documentmanagement.service;

public interface EmailService {
    void sendNewAccountEmail(String name, String toEmail, String token);
    void sendPasswordResetEmail(String name, String toEmail, String token);
}
