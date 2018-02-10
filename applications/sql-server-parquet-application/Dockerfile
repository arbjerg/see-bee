FROM openjdk:9-jre

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/seebee/seebee.jar"]

RUN mkdir /parquet

ARG JAR_FILE
COPY target/${JAR_FILE} /usr/share/seebee/seebee.jar

COPY default-config.yaml /seebee.yaml

VOLUME /parquet