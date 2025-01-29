package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.exception.MissingPropertyException;
import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.helper.ServiceProperties;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;

    public JavalinService() {
        log.info("Initializing {}", ServiceProperties.getProperty(Constants.SERVICE_NAME).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_NAME)));
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.router.apiBuilder(routes());
            javalinConfig.router.contextPath = ServiceProperties.getProperty(Constants.SERVICE_CONTEXT_PATH).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_CONTEXT_PATH));
        });
        log.info("Successfully initialized {}", ServiceProperties.getProperty(Constants.SERVICE_NAME).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_NAME)));
    }

    private EndpointGroup routes() {
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", ServiceProperties.getProperty(Constants.SERVICE_NAME).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_NAME)));
        javalin.start(Integer.parseInt(ServiceProperties.getProperty(Constants.SERVICE_PORT).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_PORT))));
        log.info("Successfully started {}", ServiceProperties.getProperty(Constants.SERVICE_NAME).orElseThrow(() -> new MissingPropertyException(Constants.SERVICE_NAME)));
    }
}