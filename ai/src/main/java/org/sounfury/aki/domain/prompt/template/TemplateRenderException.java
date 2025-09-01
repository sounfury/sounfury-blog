package org.sounfury.aki.domain.prompt.template;

/**
 * 模板渲染异常
 */
public class TemplateRenderException extends RuntimeException {
    
    public TemplateRenderException(String message) {
        super(message);
    }
    
    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TemplateRenderException(Throwable cause) {
        super(cause);
    }
}
