# Multi-stage build for jSS7 MAP Load Test
FROM eclipse-temurin:17-jdk as builder

# Install Ant manually
RUN apt-get update && \
    apt-get install -y wget && \
    wget -q https://downloads.apache.org/ant/binaries/apache-ant-1.10.14-bin.tar.gz && \
    tar -xzf apache-ant-1.10.14-bin.tar.gz -C /opt && \
    ln -s /opt/apache-ant-1.10.14/bin/ant /usr/local/bin/ant && \
    rm apache-ant-1.10.14-bin.tar.gz && \
    apt-get remove -y wget && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:17-jdk

# Install tcpdump, curl, and libsctp (runtime)
RUN apt-get update && \
    apt-get install -y tcpdump libpcap0.8 curl libsctp1 && \
    rm -rf /var/lib/apt/lists/*

# Download javolution only (xstream/xmlpull/mxparser removed - now using Jackson XML)
RUN mkdir -p /opt/target/load && \
    curl -L -o /opt/target/load/javolution.jar https://repo1.maven.org/maven2/org/javolution/javolution/5.3.1/javolution-5.3.1.jar

# Copy Ant from builder
COPY --from=builder /opt/apache-ant-1.10.14 /opt/ant
RUN ln -s /opt/ant/bin/ant /usr/local/bin/ant

# Set working directory
WORKDIR /opt/jss7-load-test

# Create directories
RUN mkdir -p server client target/load

# Copy files
COPY mo_sms_build.xml .
COPY target/load/*.jar /opt/target/load/
COPY src/main/resources src/main/resources
COPY src/main/resources/log4j.properties .

# Environment variables
ENV TEST_SERVER_CHANNEL_TYPE=tcp
ENV TEST_SERVER_HOST_IP=0.0.0.0
ENV TEST_SERVER_HOST_PORT=8011
ENV TEST_SERVER_EXTRA_HOST_ADDRESS=-1
ENV TEST_SERVER_PEER_IP=127.0.0.1
ENV TEST_SERVER_PEER_PORT=8012
ENV TEST_SERVER_AS_FUNCTIONALITY=IPSP
ENV TEST_SERVER_RC=101
ENV TEST_SERVER_NA=102
ENV TEST_SERVER_ORIGINATING_PC=1
ENV TEST_SERVER_DESTINATION_PC=2
ENV TEST_SERVER_SI=3
ENV TEST_SERVER_NI=2
ENV TEST_SERVER_SSN=8
ENV TEST_SERVER_REMOTE_SSN=8

ENV TEST_CLIENT_NUM_OF_DIALOGS=60000
ENV TEST_CLIENT_CONCURRENT_DIALOG=1000
ENV TEST_CLIENT_CHANNEL_TYPE=tcp
ENV TEST_CLIENT_HOST_IP=127.0.0.1
ENV TEST_CLIENT_HOST_PORT=8011
ENV TEST_CLIENT_EXTRA_HOST_ADDRESS=-1
ENV TEST_CLIENT_PEER_IP=127.0.0.1
ENV TEST_CLIENT_PEER_PORT=8012
ENV TEST_CLIENT_AS_FUNCTIONALITY=IPSP
ENV TEST_CLIENT_RC=101
ENV TEST_CLIENT_NA=102
ENV TEST_CLIENT_ORIGINATING_PC=1
ENV TEST_CLIENT_DESTINATION_PC=2
ENV TEST_CLIENT_SI=3
ENV TEST_CLIENT_NI=2
ENV TEST_CLIENT_SSN=8
ENV TEST_CLIENT_REMOTE_SSN=8
ENV TEST_CLIENT_CLIENT_ADDRESS=1111112
ENV TEST_CLIENT_SERVER_ADDRESS=9960639999

ENV JVM_OPTS="-Xms2048m -Xmx2048m -Xmn128m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"

EXPOSE 8011 8012

COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["server"]
