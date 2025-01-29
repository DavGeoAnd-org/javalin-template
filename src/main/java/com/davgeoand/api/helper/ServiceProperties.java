package com.davgeoand.api.helper;

import io.opentelemetry.instrumentation.resources.ManifestResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties;
import io.opentelemetry.sdk.resources.Resource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProperties {
    @Getter
    private static TreeMap<String, String> propertiesTreeMap;
    private static final Pattern propertyPattern = Pattern.compile("\\[\\[.*::.*]]");

    public static void init(String... files) {
        log.info("Initializing service properties");
        Properties properties = new Properties();
        for (String file : files) {
            try {
                properties.load(ServiceProperties.class.getClassLoader().getResourceAsStream(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                System.exit(1);
            }
        }
        assessFilesProperties(properties);
        setOtlpProperties(properties);
        ServiceProperties.propertiesTreeMap = new TreeMap<>();
        properties.forEach((key, value) -> propertiesTreeMap.put(key.toString(), value.toString()));
        log.info("Successfully initialized service properties");
    }

    private static void setOtlpProperties(Properties properties) {
        log.info("Setting opentelemetry properties");
        ServiceLoader<ResourceProvider> loader = ServiceLoader.load(ResourceProvider.class);
        loader.forEach(resourceProvider -> {
            try {
                if (resourceProvider instanceof ManifestResourceProvider manifestResourceProvider) {
                    manifestResourceProvider.shouldApply(DefaultConfigProperties.create(new HashMap<>()), Resource.empty());
                }
                resourceProvider.createResource(DefaultConfigProperties.create(new HashMap<>())).getAttributes().forEach(((attributeKey, o) -> {
                    log.info(attributeKey.getKey() + ": " + o.toString());
                    properties.put(attributeKey.getKey(), o.toString());
                }));
            } catch (Exception e) {
                log.warn("Issue with provider {}", resourceProvider.getClass());
            }
        });
        log.info("Successfully set opentelemetry properties");
    }

    private static void assessFilesProperties(Properties properties) {
        properties.forEach((key, value) -> {
            log.info("{}: {}", key, value);
            String valueStr = value.toString();
            Matcher match = propertyPattern.matcher(valueStr);
            if (match.find()) {
                log.info("Property that uses env: {} with value {}", key, value);
                String valueStrUpdated = valueStr.replace("[", "").replace("]", "");
                String[] valueSplit = valueStrUpdated.split("::");
                String envValue = System.getenv(valueSplit[0]);
                if (envValue != null) {
                    properties.replace(key, envValue);
                    log.info(key + " is using env value: " + envValue);
                } else {
                    properties.replace(key, valueSplit[1]);
                    log.info(key + " is using default value: " + valueSplit[1]);
                }
            }
        });
    }

    public static Optional<String> getProperty(String property) {
        return Optional.ofNullable(propertiesTreeMap.get(property));
    }
}