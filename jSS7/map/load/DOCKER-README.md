# jSS7 MAP Load Test - Docker Setup

Docker containerization for jSS7 MAP Load Test using MO SMS build configuration.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- Maven 3.8+ (for building)

## Quick Start

### 1. Build Docker Image

```bash
# Option 1: Use the build script
chmod +x build-docker.sh
./build-docker.sh

# Option 2: Manual build
mvn clean install -DskipTests
mvn install -Passemble -DskipTests
docker build -t jss7-map-load-test:latest .
```

### 2. Run with Docker Compose (Recommended)

```bash
# Start both server and client
docker-compose up

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### 3. Run Manually

```bash
# Start server
docker run -it --rm \
  -p 8011:8011 \
  -p 8012:8012 \
  -e TEST_SERVER_HOST_IP=0.0.0.0 \
  jss7-map-load-test:latest server

# Start client (in another terminal)
docker run -it --rm \
  --network host \
  -e TEST_CLIENT_PEER_IP=127.0.0.1 \
  jss7-map-load-test:latest client
```

## Configuration

### Environment Variables - Server

| Variable | Default | Description |
|----------|---------|-------------|
| `TEST_SERVER_CHANNEL_TYPE` | sctp | Transport protocol (sctp/tcp) |
| `TEST_SERVER_HOST_IP` | 0.0.0.0 | Server bind address |
| `TEST_SERVER_HOST_PORT` | 8011 | Server SCTP/TCP port |
| `TEST_SERVER_PEER_IP` | 127.0.0.1 | Peer IP address |
| `TEST_SERVER_PEER_PORT` | 8012 | Peer port |
| `TEST_SERVER_AS_FUNCTIONALITY` | IPSP | AS mode: AS, SGW, IPSP |
| `TEST_SERVER_RC` | 101 | Routing Context |
| `TEST_SERVER_NA` | 102 | Network Appearance |
| `TEST_SERVER_ORIGINATING_PC` | 1 | Originating Point Code |
| `TEST_SERVER_DESTINATION_PC` | 2 | Destination Point Code |
| `TEST_SERVER_SI` | 3 | Service Indicator |
| `TEST_SERVER_NI` | 2 | Network Indicator |
| `TEST_SERVER_SSN` | 8 | Subsystem Number |
| `TEST_SERVER_REMOTE_SSN` | 8 | Remote Subsystem Number |

### Environment Variables - Client

| Variable | Default | Description |
|----------|---------|-------------|
| `TEST_CLIENT_NUM_OF_DIALOGS` | 1440000 | Total number of dialogs |
| `TEST_CLIENT_CONCURRENT_DIALOG` | 400 | Concurrent dialogs |
| `TEST_CLIENT_HOST_IP` | 127.0.0.1 | Client host IP |
| `TEST_CLIENT_HOST_PORT` | 8011 | Client host port |
| `TEST_CLIENT_PEER_IP` | 127.0.0.1 | Peer (server) IP |
| `TEST_CLIENT_PEER_PORT` | 8012 | Peer (server) port |
| `TEST_CLIENT_CLIENT_ADDRESS` | 1111112 | Client SCCP Address |
| `TEST_CLIENT_SERVER_ADDRESS` | 9960639999 | Server SCCP Address |

## Advanced Usage

### Custom Test Scenario

```bash
# High load test
docker run -it --rm \
  -e TEST_CLIENT_NUM_OF_DIALOGS=10000000 \
  -e TEST_CLIENT_CONCURRENT_DIALOG=1000 \
  jss7-map-load-test:latest client

# TCP instead of SCTP
docker run -it --rm \
  -e TEST_SERVER_CHANNEL_TYPE=tcp \
  -p 8011:8011/tcp \
  jss7-map-load-test:latest server
```

### Distributed Testing

```yaml
# docker-compose-distributed.yml
version: '3.8'
services:
  server:
    image: jss7-map-load-test:latest
    command: server
    ports:
      - "8011:8011"
    environment:
      TEST_SERVER_HOST_IP: 0.0.0.0
      TEST_SERVER_PEER_IP: 10.0.0.5  # Client IP
  
  client:
    image: jss7-map-load-test:latest
    command: client
    environment:
      TEST_CLIENT_PEER_IP: 10.0.0.10  # Server IP
```

### Debug Mode

```bash
# Access shell inside container
docker run -it --rm --entrypoint /bin/sh jss7-map-load-test:latest

# Check assembled JARs
docker run -it --rm jss7-map-load-test:latest bash
ls -la /opt/jss7-load-test/target/load/
```

## Troubleshooting

### Issue: `target/load` directory not found

**Solution**: Build the Maven project first:
```bash
mvn clean install -DskipTests
mvn install -Passemble -DskipTests
```

### Issue: SCTP not supported in Docker

**Solution**: Use TCP mode or run with `--privileged`:
```bash
docker run --privileged -it --rm jss7-map-load-test:latest server
```

### Issue: Connection refused

**Solution**: Check network connectivity and firewall rules:
```bash
# Test connectivity
docker run --rm busybox nc -zv <server-ip> 8011
```

## Files

- `Dockerfile` - Docker image definition
- `docker-entrypoint.sh` - Entrypoint script
- `docker-compose.yml` - Compose configuration
- `build-docker.sh` - Build script
- `DOCKER-README.md` - This file

## Notes

- Default Java version: 17 (Eclipse Temurin)
- Default memory: 2GB heap, 256MB metaspace
- SCTP support requires Linux kernel with SCTP module
- For production use, adjust JVM options based on hardware
