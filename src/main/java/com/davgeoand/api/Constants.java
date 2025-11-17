package com.davgeoand.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    // Service
    public static final String SERVICE_NAME = ServiceProperties.getProperty("service.name").orElse("javalin-template");
    public static final String SERVICE_PORT = StringUtils.defaultIfBlank(System.getenv("SERVICE_PORT"), "8080");
    public static final String SERVICE_CONTEXT_PATH = StringUtils.defaultIfBlank(System.getenv("SERVICE_CONTEXT_PATH"), "/template");
}