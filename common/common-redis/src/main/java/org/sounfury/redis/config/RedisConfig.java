package org.sounfury.redis.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.config.Config;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.sounfury.redis.manager.PlusSpringCacheManager;
import org.sounfury.redis.serialize.FastJsonRedisSerializer;
import org.sounfury.redis.serialize.JooqUTypeSerializers;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class RedisConfig {
    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer(){
        // 配置 Redis 地址
     return config -> {
         config.setCodec(createCodec());
     };
    }



    private CompositeCodec createCodec() {
        // 自定义 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(createJavaTimeModule());
        objectMapper.registerModule(createJooqModule());  // 添加 JOOQ 模块
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, objectMapper);
        return new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
    }

    /**
     * 配置 LocalDateTime 的序列化与反序列化
     */
    private JavaTimeModule createJavaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    /**
     * 配置 JOOQ 的序列化与反序列化
     * @return
     */
    private SimpleModule createJooqModule() {
        SimpleModule module = new SimpleModule("JooqModule");

        // 注册 UInteger 序列化器
        module.addSerializer(UInteger.class, new JooqUTypeSerializers.UIntegerSerializer());
        module.addDeserializer(UInteger.class, new JooqUTypeSerializers.UIntegerDeserializer());

        // 注册 ULong 序列化器
        module.addSerializer(ULong.class, new JooqUTypeSerializers.ULongSerializer());
        module.addDeserializer(ULong.class, new JooqUTypeSerializers.ULongDeserializer());

        // 注册 UShort 序列化器
        module.addSerializer(UShort.class, new JooqUTypeSerializers.UShortSerializer());
        module.addDeserializer(UShort.class, new JooqUTypeSerializers.UShortDeserializer());

        // 注册 UByte 序列化器
        module.addSerializer(UByte.class, new JooqUTypeSerializers.UByteSerializer());
        module.addDeserializer(UByte.class, new JooqUTypeSerializers.UByteDeserializer());

        return module;
    }



    /**
     * 自定义缓存管理器 整合spring-cache
     */
    @Bean
    public CacheManager cacheManager() {
        return new PlusSpringCacheManager();
    }

}