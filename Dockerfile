FROM davgeoand9/otel-java-agent:21.0.9_10-2.26.0

COPY ./target/javalin-template.jar javalin-template.jar
COPY ./target/lib lib

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "javalin-template.jar"]