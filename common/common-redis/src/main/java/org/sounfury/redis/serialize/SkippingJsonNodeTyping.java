package org.sounfury.redis.serialize;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class SkippingJsonNodeTyping extends ObjectMapper.DefaultTypeResolverBuilder {

    public SkippingJsonNodeTyping(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
        super(t, ptv);
    }

    @Override
    public boolean useForType(JavaType t) {
        Class<?> raw = t.getRawClass();
        // 1) 排除 Jackson 树模型
        if (JsonNode.class.isAssignableFrom(raw)) {
            return false;
        }
        // 2) 其它你不想套默认多态的类型也可在这里排除（可选）
        // if (raw.getName().startsWith("com.fasterxml.jackson.databind.node")) return false;

        // 其余保持原有判定逻辑
        return super.useForType(t);
    }
}