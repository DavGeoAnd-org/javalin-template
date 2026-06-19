FROM davidgandalcio/otel-java-agent:21.0.11_10-2.28.1

COPY ./target/javalin-template.jar javalin-template.jar
COPY ./target/lib lib

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "javalin-template.jar"]