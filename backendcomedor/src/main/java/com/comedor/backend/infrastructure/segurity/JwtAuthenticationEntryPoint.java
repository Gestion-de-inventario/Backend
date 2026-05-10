package com.comedor.backend.infrastructure.segurity;

import com.comedor.backend.infrastructure.adapters.in.web.exceptions.CustomErrorResponse;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        String exceptionMsg = (String) request.getAttribute("exception");
        String requestUri = (String) request.getAttribute("jakarta.servlet.forward.request_uri");
        if (requestUri == null) {
            requestUri = request.getRequestURI();
        }

        if (exceptionMsg == null) {
            exceptionMsg = "Token inválido o no presente";
        }


        CustomErrorResponse error = new CustomErrorResponse(
                LocalDateTime.now(),
                exceptionMsg,
                requestUri
        );


        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}