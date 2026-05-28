#!/bin/bash

set -euo pipefail

# server side
mkdir -p postgres_ssl
cd postgres_ssl

openssl genrsa -out ca.key 4096
chmod 600 ca.key
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.crt -subj "/CN=postgresCA"

openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -subj "/CN=localhost"
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365 -sha256

# client side
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr -subj "/CN=auth_postgres_user"
openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt -days 365 -sha256
openssl pkcs8 -topk8 -inform PEM -outform PEM -in client.key -out client.pkcs8.key -nocrypt

# Set permissions
sudo chmod 600 server.key
if [ "$(uname -s)" = "Linux" ]; then
    sudo chown 70 server.key
fi

rm ca.srl client.csr server.csr

sudo cp ca.crt server.crt server.key client.pkcs8.key ../auth-service/src/test/resources/ssl