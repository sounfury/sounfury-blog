package org.sounfury.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.redis.serialize.JooqJsonModule;
import org.sounfury.redis.serialize.SkippingJsonNodeTyping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

//日志
@Slf4j
@Configuration
public class JacksonConfig {

  private JavaTimeModule createJavaTimeModule() {
    JavaTimeModule module = new JavaTimeModule();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
    return module;
  }

  @Bean @Primary
  public ObjectMapper primaryObjectMapper() {
    return Jackson2ObjectMapperBuilder
            .json()
            .modulesToInstall(new JavaTimeModule())
            .build(); // 不调用 activateDefaultTyping
  }

  @Bean("polymorphicObjectMapper")
  public ObjectMapper polymorphicObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(createJavaTimeModule());
    objectMapper.registerModule(new JooqJsonModule());
    objectMapper.setTimeZone(TimeZone.getDefault());
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.WRAPPER_ARRAY
    );
    // 安全的多态类型验证器，只允许自家包
    PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType("org.sounfury.")
            .build();

    // 启用多态类型信息，排除JsonNode类型
//    SkippingJsonNodeTyping typing = new SkippingJsonNodeTyping(
//            ObjectMapper.DefaultTyping.NON_FINAL, ptv
//    );
//    typing = (SkippingJsonNodeTyping) typing
//            .init(JsonTypeInfo.Id.CLASS, null)
//            .inclusion(JsonTypeInfo.As.PROPERTY);
//
//    objectMapper.setDefaultTyping(typing);

    return objectMapper;
  }



}
