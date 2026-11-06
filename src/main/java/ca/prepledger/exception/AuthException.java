package ca.prepledger.exception;

import java.io.IOException;

/**
 * Class which encapsulates exceptions that arise from HTTP calls. Contains the
 * HTTP code and any related message that might be included in the body of an
 * HTTP response.
 * 
 * @author Cailean Bernard
 * @since v0.1.2
 */
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