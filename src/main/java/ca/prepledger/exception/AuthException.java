package ca.prepledger.exception;

import java.io.IOException;

public class AuthException extends IOException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}