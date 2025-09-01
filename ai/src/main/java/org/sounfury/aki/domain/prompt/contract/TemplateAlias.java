package org.sounfury.aki.domain.prompt.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模板别名注解
 * 用于为字段在模板中提供别名，解决字段名与模板占位符不匹配的问题
 * 
 * 使用示例：
 * <pre>
 * public class PromptContext {
 *     {@literal @}TemplateAlias("char")
 *     private final CharCtx charCtx;  // 在模板中可以使用 {{char.greeting}}
 * }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateAlias {
    
    /**
     * 模板中使用的别名
     * 
     * @return 别名字符串，不能为空
     */
    String value();
}
