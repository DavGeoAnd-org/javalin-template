package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.exception.JavalinServiceException.MissingPropertyException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = ServiceProperties.getProperty("service.name").orElseThrow(() -> new MissingPropertyException("service.name"));
    private final String SERVICE_CONTEXT_PATH = ServiceProperties.getProperty("service.context.path").orElseThrow(() -> new MissingPropertyException("service.context.path"));
    private final String SERVICE_PORT = ServiceProperties.getProperty("service.port").orElseThrow(() -> new MissingPropertyException("service.port"));

    public JavalinService() {
        log.info("Initializing {}", SERVICE_NAME);
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.router.apiBuilder(routes());
            javalinConfig.router.contextPath = SERVICE_CONTEXT_PATH;
        });
        addExceptionHandlers();
        log.info("Initialized {}", SERVICE_NAME);
    }

    @WithSpan
    private void addExceptionHandlers() {
        log.info("Adding exception handlers");
        log.info("Added exception handlers");
    }

    @WithSpan
    private EndpointGroup routes() {
        return () -> path("admin", AdminController.getAdminEndpoints());
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(SERVICE_PORT));
        log.info("Started {}", SERVICE_NAME);
    }
}