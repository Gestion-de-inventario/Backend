package com.comedor.backend.domain.exceptions;

public class MenuReportNotFoundException extends RuntimeException {
    public MenuReportNotFoundException(String message) {
        super(message);
    }
}
