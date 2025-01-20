FROM gradle:8.12-jdk21 AS builder
ARG CODEARTIFACT_AUTH_TOKEN
ENV CODEARTIFACT_AUTH_TOKEN $CODEARTIFACT_AUTH_TOKEN
ARG DD_TRACER_VERSION=1.44.1
WORKDIR /workspace
COPY --chown=gradle:gradle . /workspace/
RUN gradle build \
&&  wget -O dd-java-agent.jar https://github.com/DataDog/dd-trace-java/releases/download/v$DD_TRACER_VERSION/dd-java-agent-$DD_TRACER_VERSION.jar

FROM amazoncorretto:21.0.5-alpine AS runtime
RUN apk update && apk upgrade --no-cache
RUN addgroup -S app && adduser -H -S -s /bin/sh app -G app
RUN mkdir /app && chown -Rv app:app /app /tmp && chmod 700 -Rv /app /tmp
WORKDIR /app
USER app
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /workspace/${JAR_FILE} ./{{ cookiecutter.project_slug }}.jar
COPY --from=builder /workspace/dd-java-agent.jar ./dd-java-agent.jar

ENTRYPOINT ["java", \
            "-javaagent:/app/dd-java-agent.jar", \
            "-Xmx2048m", \
            "-jar", "/app/{{ cookiecutter.project_slug }}.jar"]