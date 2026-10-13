package com.comedor.backend.domain.exceptions;

public class MenuReportAlreadyExistException extends BusinessException {
    public MenuReportAlreadyExistException(String message) {
        super(message);
    }
}
