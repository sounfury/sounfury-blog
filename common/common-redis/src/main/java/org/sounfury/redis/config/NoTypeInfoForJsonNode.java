package org.sounfury.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
abstract class NoTypeInfoForJsonNode {}