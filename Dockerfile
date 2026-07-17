FROM davidgandalcio/otel-java-agent:25.0.3_9-2.29.0
LABEL authors="davidgandalcio"

COPY ./target/javalin-template.jar javalin-template.jar
COPY ./target/lib lib

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "javalin-template.jar"]