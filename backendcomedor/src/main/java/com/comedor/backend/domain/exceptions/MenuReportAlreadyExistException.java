package com.comedor.backend.domain.exceptions;

public class MenuReportAlreadyExistException extends RuntimeException {
    public MenuReportAlreadyExistException(String message) {
        super(message);
    }
}
