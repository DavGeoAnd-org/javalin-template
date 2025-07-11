package com.davgeoand.api.controller;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.get;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminController {
    public static @NotNull EndpointGroup getAdminEndpoints() {
        return () -> {
            get("health", AdminController::health);
        };
    }

    private static void health(@NotNull Context context) {
        log.debug("Starting admin health request");
        context.status(HttpStatus.OK);
        log.debug("Finished admin health request");
    }
}