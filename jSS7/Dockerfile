# Runtime stage with SCTP support
FROM amazoncorretto:11-alpine

# Install SCTP support
RUN apk add net-tools lksctp-tools supervisor lksctp-tools-dev

# Create workspace
RUN mkdir -p /opt/paic/jss7
WORKDIR /opt/paic/jss7

# Copy pre-built artifacts from local build
COPY map/load/target/load /opt/paic/jss7/load

# Entry point
ENTRYPOINT ["/bin/sh"]
CMD ["echo", "jSS7 MAP Load ready. JARs are in /opt/paic/jss7/load"]
