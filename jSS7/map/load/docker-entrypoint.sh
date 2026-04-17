#!/bin/sh
# Docker entrypoint for jSS7 MAP Load Test

set -e

cd /opt/jss7-load-test

# Function to run server
# Skip setup-deps since JARs are already copied at build time (no need to copy from Windows paths)
run_server() {
    echo "Starting jSS7 MAP Load Test Server..."
    echo "Configuration:"
    echo "  Channel Type: ${TEST_SERVER_CHANNEL_TYPE}"
    echo "  Host: ${TEST_SERVER_HOST_IP}:${TEST_SERVER_HOST_PORT}"
    echo "  Peer: ${TEST_SERVER_PEER_IP}:${TEST_SERVER_PEER_PORT}"
    echo "  AS Functionality: ${TEST_SERVER_AS_FUNCTIONALITY}"
    echo "  Point Codes: ${TEST_SERVER_ORIGINATING_PC} -> ${TEST_SERVER_DESTINATION_PC}"
    
    # Clean up previous runs
    rm -rf server/*
    mkdir -p server
    rm -f log4j-server.log
    
    # Set classpath property for ant (skip setup-deps - JARs already in /opt/target/load)
    ant -f mo_sms_build.xml server \
        -Dassemble.dir=/opt/target/load \
        -Dbasedir=/opt/jss7-load-test \
        -Dtest.server.channelType="${TEST_SERVER_CHANNEL_TYPE}" \
        -Dtest.server.hostIp="${TEST_SERVER_HOST_IP}" \
        -Dtest.server.hostPort="${TEST_SERVER_HOST_PORT}" \
        -Dtest.server.extraHostAddress="${TEST_SERVER_EXTRA_HOST_ADDRESS}" \
        -Dtest.server.peerIp="${TEST_SERVER_PEER_IP}" \
        -Dtest.server.peerPort="${TEST_SERVER_PEER_PORT}" \
        -Dtest.server.asFunctionality="${TEST_SERVER_AS_FUNCTIONALITY}" \
        -Dtest.server.rc="${TEST_SERVER_RC}" \
        -Dtest.server.na="${TEST_SERVER_NA}" \
        -Dtest.server.originatingPc="${TEST_SERVER_ORIGINATING_PC}" \
        -Dtest.server.destinationPc="${TEST_SERVER_DESTINATION_PC}" \
        -Dtest.server.si="${TEST_SERVER_SI}" \
        -Dtest.server.ni="${TEST_SERVER_NI}" \
        -Dtest.server.ssn="${TEST_SERVER_SSN}" \
        -Dtest.server.remoteSsn="${TEST_SERVER_REMOTE_SSN}"
}

# Function to run client
# Skip setup-deps since JARs are already copied at build time (no need to copy from Windows paths)
run_client() {
    echo "Starting jSS7 MAP Load Test Client..."
    echo "Configuration:"
    echo "  Num of Dialogs: ${TEST_CLIENT_NUM_OF_DIALOGS}"
    echo "  Concurrent Dialogs: ${TEST_CLIENT_CONCURRENT_DIALOG}"
    echo "  Channel Type: ${TEST_CLIENT_CHANNEL_TYPE}"
    echo "  Host: ${TEST_CLIENT_HOST_IP}:${TEST_CLIENT_HOST_PORT}"
    echo "  Peer: ${TEST_CLIENT_PEER_IP}:${TEST_CLIENT_PEER_PORT}"
    
    # Clean up previous runs
    rm -rf client/*
    mkdir -p client
    rm -f log4j-client.log
    
    # Run client (skip setup-deps - JARs already in /opt/target/load)
    ant -f mo_sms_build.xml client \
        -Dassemble.dir=/opt/target/load \
        -Dbasedir=/opt/jss7-load-test \
        -Dtest.client.numOfDialogs="${TEST_CLIENT_NUM_OF_DIALOGS}" \
        -Dtest.client.concurrentDialog="${TEST_CLIENT_CONCURRENT_DIALOG}" \
        -Dtest.client.channelType="${TEST_CLIENT_CHANNEL_TYPE}" \
        -Dtest.client.hostIp="${TEST_CLIENT_HOST_IP}" \
        -Dtest.client.hostPort="${TEST_CLIENT_HOST_PORT}" \
        -Dtest.client.extraHostAddress="${TEST_CLIENT_EXTRA_HOST_ADDRESS}" \
        -Dtest.client.peerIp="${TEST_CLIENT_PEER_IP}" \
        -Dtest.client.peerPort="${TEST_CLIENT_PEER_PORT}" \
        -Dtest.client.asFunctionality="${TEST_CLIENT_AS_FUNCTIONALITY}" \
        -Dtest.client.rc="${TEST_CLIENT_RC}" \
        -Dtest.client.na="${TEST_CLIENT_NA}" \
        -Dtest.client.originatingPc="${TEST_CLIENT_ORIGINATING_PC}" \
        -Dtest.client.destinationPc="${TEST_CLIENT_DESTINATION_PC}" \
        -Dtest.client.si="${TEST_CLIENT_SI}" \
        -Dtest.client.ni="${TEST_CLIENT_NI}" \
        -Dtest.client.ssn="${TEST_CLIENT_SSN}" \
        -Dtest.client.remoteSsn="${TEST_CLIENT_REMOTE_SSN}" \
        -Dtest.client.clientAddress="${TEST_CLIENT_CLIENT_ADDRESS}" \
        -Dtest.client.serverAddress="${TEST_CLIENT_SERVER_ADDRESS}"
}

# Function to run assemble
run_assemble() {
    echo "Assembling jSS7 MAP Load Test..."
    ant -f mo_sms_build.xml assemble
}

# Main command handler
case "${1}" in
    server)
        run_server
        ;;
    client)
        run_client
        ;;
    assemble)
        run_assemble
        ;;
    clean)
        ant -f mo_sms_build.xml clean
        ;;
    bash|sh)
        /bin/sh
        ;;
    *)
        echo "Usage: $0 {server|client|assemble|clean|bash}"
        echo ""
        echo "Commands:"
        echo "  server    - Run the load test server"
        echo "  client    - Run the load test client"
        echo "  assemble  - Build/assemble the load test"
        echo "  clean     - Clean up logs and directories"
        echo "  bash      - Open a shell"
        echo ""
        echo "Environment variables for server:"
        echo "  TEST_SERVER_HOST_IP, TEST_SERVER_HOST_PORT"
        echo "  TEST_SERVER_PEER_IP, TEST_SERVER_PEER_PORT"
        echo "  TEST_SERVER_AS_FUNCTIONALITY (AS|SGW|IPSP)"
        echo ""
        echo "Environment variables for client:"
        echo "  TEST_CLIENT_NUM_OF_DIALOGS"
        echo "  TEST_CLIENT_CONCURRENT_DIALOG"
        echo "  TEST_CLIENT_HOST_IP, TEST_CLIENT_HOST_PORT"
        echo "  TEST_CLIENT_PEER_IP, TEST_CLIENT_PEER_PORT"
        exit 1
        ;;
esac
