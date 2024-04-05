package com.adoustar.documentmanagement.utils;

import com.adoustar.documentmanagement.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class RequestUtil {
    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(LocalDateTime.now().toString(), status.value(), request.getRequestURI(), HttpStatus.valueOf(status.value()), message, EMPTY, data);
    }
}
