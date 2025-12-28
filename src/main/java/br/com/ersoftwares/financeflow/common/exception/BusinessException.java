package br.com.ersoftwares.financeflow.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = null;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
