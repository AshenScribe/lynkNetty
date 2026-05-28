#!/bin/bash
set -e

echo "$SERVER_CRT" > /var/lib/postgresql/server.crt
echo "$SERVER_KEY" > /var/lib/postgresql/server.key
echo "$ROOT_CA_CRT" > /var/lib/postgresql/rootCA.crt

chmod 600 /var/lib/postgresql/server.* /var/lib/postgresql/rootCA.crt
chown postgres:postgres /var/lib/postgresql/server.* /var/lib/postgresql/rootCA.crt

docker-entrypoint.sh postgres
