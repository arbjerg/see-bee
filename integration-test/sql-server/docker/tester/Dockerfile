FROM maven:3.5-jdk-9-slim

ENV DIR=/usr/src/seebee/tester

WORKDIR ${DIR}

COPY pom.xml ${DIR}
COPY src ${DIR}/src

RUN mvn -T 2C -Dtest=TestIntegrity test

VOLUME /parquet

ENTRYPOINT ["mvn", "test"]

CMD []