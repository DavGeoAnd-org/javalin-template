FROM dgandalcio/java-otel-agent:21.0.5_11-2.10.0

COPY ./target/javalin-template.jar javalin-template.jar
COPY ./target/lib lib

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "javalin-template.jar"]