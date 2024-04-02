package com.adoustar.documentmanagement.utils;

public class EmailUtil {

    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + ",\n\nYour new account has been created." +
                "Please click on the link below to verify your account.\n\n" +
                getVerficationUrl(host, token) + "\n\nThe Support Team";
    }

    public static String getResetPasswordMessage(String name, String host, String token) {
        return "Hello " + name +
                ",\n\nPlease click on the link below to reset your account.\n\n" +
                getResetPasswordUrl(host, token) + "\n\nThe Support Team";
    }

    public static String getVerficationUrl(String host, String token) {
        return host + "/verify/account?token=" + token;
    }

    public static String getResetPasswordUrl(String host, String token) {
        return host + "/verify/password?token=" + token;
    }
}
