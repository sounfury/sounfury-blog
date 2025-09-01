package org.sounfury.aki.domain.shared.exception;

/**
 * 领域异常基类
 * 所有领域层异常的基类
 */
public abstract class DomainException extends RuntimeException {
    
    private final String errorCode;
    
    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s]: %s", 
                getClass().getSimpleName(), errorCode, getMessage());
    }
}
